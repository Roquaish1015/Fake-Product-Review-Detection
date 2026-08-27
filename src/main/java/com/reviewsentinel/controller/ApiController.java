package com.reviewsentinel.controller;

import com.reviewsentinel.model.AnalysisResult;
import com.reviewsentinel.service.ReviewAnalysisService;
import com.reviewsentinel.service.UrlProductExtractor;
import com.reviewsentinel.service.UrlProductExtractor.UrlBatchReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ReviewAnalysisService analysisService;
    private final UrlProductExtractor urlProductExtractor;

    public ApiController(ReviewAnalysisService analysisService, UrlProductExtractor urlProductExtractor) {
        this.analysisService = analysisService;
        this.urlProductExtractor = urlProductExtractor;
    }

    public static class AnalyzeRequest {
        private String productName;
        private Integer rating;
        private String reviewTitle;
        private String reviewContent;
        private Boolean verifiedPurchase;

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getReviewTitle() { return reviewTitle; }
        public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }

        public String getReviewContent() { return reviewContent; }
        public void setReviewContent(String reviewContent) { this.reviewContent = reviewContent; }

        public Boolean getVerifiedPurchase() { return verifiedPurchase; }
        public void setVerifiedPurchase(Boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    }

    public static class UrlAnalyzeRequest {
        private String url;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResult> analyzeReview(@RequestBody AnalyzeRequest request) {
        AnalysisResult result = analysisService.analyzeAndSave(
                request.getProductName(),
                request.getRating(),
                request.getReviewTitle(),
                request.getReviewContent(),
                request.getVerifiedPurchase()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping({"/analyze-url", "/reviews/analyze-url"})
    public ResponseEntity<?> analyzeProductUrl(@RequestBody UrlAnalyzeRequest request) {
        try {
            if (request.getUrl() == null || !request.getUrl().toLowerCase().startsWith("http")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid URL: Must start with http:// or https://"));
            }

            UrlBatchReport report = urlProductExtractor.processProductUrl(request.getUrl());
            for (AnalysisResult ar : report.getAnalyzedReviews()) {
                analysisService.analyzeAndSave(
                        ar.getProductName(),
                        ar.getRating(),
                        ar.getReviewTitle(),
                        ar.getOriginalText(),
                        ar.getVerifiedPurchase()
                );
            }
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(analysisService.getDashboardStats());
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long id) {
        boolean deleted = analysisService.deleteReview(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Review deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Review not found"));
        }
    }
}
