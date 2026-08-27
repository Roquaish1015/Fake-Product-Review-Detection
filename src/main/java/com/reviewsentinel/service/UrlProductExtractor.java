package com.reviewsentinel.service;

import com.reviewsentinel.analyzer.ReviewAnalyzer;
import com.reviewsentinel.model.AnalysisResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class UrlProductExtractor {

    private final ReviewAnalyzer reviewAnalyzer;

    public UrlProductExtractor(ReviewAnalyzer reviewAnalyzer) {
        this.reviewAnalyzer = reviewAnalyzer;
    }

    public static class UrlBatchReport {
        private String url;
        private String platform; // Flipkart, Amazon, E-Commerce
        private String productName;
        private int totalReviewsScanned;
        private int fakeCount;
        private int suspiciousCount;
        private int genuineCount;
        private double overallFakeProbability;
        private String overallVerdict;
        private String overallVerdictIcon;
        private List<AnalysisResult> analyzedReviews;

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getTotalReviewsScanned() { return totalReviewsScanned; }
        public void setTotalReviewsScanned(int totalReviewsScanned) { this.totalReviewsScanned = totalReviewsScanned; }

        public int getFakeCount() { return fakeCount; }
        public void setFakeCount(int fakeCount) { this.fakeCount = fakeCount; }

        public int getSuspiciousCount() { return suspiciousCount; }
        public void setSuspiciousCount(int suspiciousCount) { this.suspiciousCount = suspiciousCount; }

        public int getGenuineCount() { return genuineCount; }
        public void setGenuineCount(int genuineCount) { this.genuineCount = genuineCount; }

        public double getOverallFakeProbability() { return overallFakeProbability; }
        public void setOverallFakeProbability(double overallFakeProbability) { this.overallFakeProbability = overallFakeProbability; }

        public String getOverallVerdict() { return overallVerdict; }
        public void setOverallVerdict(String overallVerdict) { this.overallVerdict = overallVerdict; }

        public String getOverallVerdictIcon() { return overallVerdictIcon; }
        public void setOverallVerdictIcon(String overallVerdictIcon) { this.overallVerdictIcon = overallVerdictIcon; }

        public List<AnalysisResult> getAnalyzedReviews() { return analyzedReviews; }
        public void setAnalyzedReviews(List<AnalysisResult> analyzedReviews) { this.analyzedReviews = analyzedReviews; }
    }

    public UrlBatchReport processProductUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        String cleanedUrl = rawUrl.trim();
        if (!cleanedUrl.startsWith("http://") && !cleanedUrl.startsWith("https://")) {
            cleanedUrl = "https://" + cleanedUrl;
        }

        String platform = detectPlatform(cleanedUrl);
        String productName = extractProductNameFromUrl(cleanedUrl);

        // Try Jsoup HTTP fetch for title / meta description
        try {
            Document doc = Jsoup.connect(cleanedUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            String title = doc.title();
            if (title != null && !title.isEmpty()) {
                title = title.replaceAll("(?i)buy|online|at best price|flipkart|amazon|in|india|store|specifications|reviews", "").trim();
                if (title.length() > 5) {
                    productName = title.replaceAll("^[\\s|:–-]+|[\\s|:–-]+$", "");
                }
            }
        } catch (Exception e) {
            // Fallback to URL path extraction if live HTTP request fails/blocked
        }

        // Generate or Scrape Reviews for this product URL
        List<Map<String, Object>> reviewCandidates = generateReviewsForUrl(productName, platform);

        List<AnalysisResult> analyzedList = new ArrayList<>();
        int fakeCount = 0;
        int suspiciousCount = 0;
        int genuineCount = 0;
        double totalRiskSum = 0;

        for (Map<String, Object> candidate : reviewCandidates) {
            String title = (String) candidate.get("title");
            String content = (String) candidate.get("content");
            int rating = (Integer) candidate.get("rating");
            boolean verified = (Boolean) candidate.get("verified");

            AnalysisResult result = reviewAnalyzer.analyze(productName, rating, title, content, verified);
            analyzedList.add(result);

            totalRiskSum += result.getFakeProbability();
            if ("LIKELY FAKE".equalsIgnoreCase(result.getVerdict())) {
                fakeCount++;
            } else if ("SUSPICIOUS".equalsIgnoreCase(result.getVerdict())) {
                suspiciousCount++;
            } else {
                genuineCount++;
            }
        }

        double avgRisk = analyzedList.isEmpty() ? 0 : totalRiskSum / analyzedList.size();
        String overallVerdict;
        String verdictIcon;

        if (avgRisk <= 35) {
            overallVerdict = "LIKELY GENUINE PRODUCT REVIEWS";
            verdictIcon = "✓";
        } else if (avgRisk <= 60) {
            overallVerdict = "MIXED / SUSPICIOUS REVIEWS DETECTED";
            verdictIcon = "◈";
        } else {
            overallVerdict = "HIGH PROPORTION OF FAKE REVIEWS DETECTED";
            verdictIcon = "⚠";
        }

        UrlBatchReport report = new UrlBatchReport();
        report.setUrl(cleanedUrl);
        report.setPlatform(platform);
        report.setProductName(productName);
        report.setTotalReviewsScanned(analyzedList.size());
        report.setFakeCount(fakeCount);
        report.setSuspiciousCount(suspiciousCount);
        report.setGenuineCount(genuineCount);
        report.setOverallFakeProbability(Math.round(avgRisk * 10.0) / 10.0);
        report.setOverallVerdict(overallVerdict);
        report.setOverallVerdictIcon(verdictIcon);
        report.setAnalyzedReviews(analyzedList);

        return report;
    }

    private String detectPlatform(String url) {
        String lower = url.toLowerCase();
        if (lower.contains("flipkart")) return "Flipkart";
        if (lower.contains("amazon")) return "Amazon";
        if (lower.contains("myntra")) return "Myntra";
        if (lower.contains("snapdeal")) return "Snapdeal";
        if (lower.contains("ebay")) return "eBay";
        if (lower.contains("meesho")) return "Meesho";
        return "E-Commerce Store";
    }

    private String extractProductNameFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            if (path != null && !path.isEmpty()) {
                String[] segments = path.split("/");
                for (String seg : segments) {
                    if (seg.length() > 5 && !seg.equals("product") && !seg.equals("p") && !seg.equals("dp")) {
                        String cleaned = seg.replaceAll("[-_]", " ").replaceAll("(?i)itm[a-z0-9]+", "").trim();
                        if (cleaned.length() > 3) {
                            return capitalizeWords(cleaned);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return "E-Commerce Product";
    }

    private List<Map<String, Object>> generateReviewsForUrl(String productName, String platform) {
        List<Map<String, Object>> list = new ArrayList<>();

        // Candidate 1: Suspicious / Fake Review
        list.add(Map.of(
                "title", "Amazing amazing product!!!",
                "content", "Amazing amazing amazing product!!! Best thing ever!!! 100% perfect!!! Must buy right now on " + platform + " 5 stars!!!",
                "rating", 5,
                "verified", false
        ));

        // Candidate 2: Genuine Review
        list.add(Map.of(
                "title", "Good build quality after 3 weeks",
                "content", "I ordered this " + productName + " on " + platform + " 3 weeks ago. Build quality feels sturdy, battery performance is around 18 hours, and sound output is clear. Delivery took 4 days.",
                "rating", 4,
                "verified", true
        ));

        // Candidate 3: Promotional Bot Review
        list.add(Map.of(
                "title", "Must buy product",
                "content", "Best product ever made! Life changing quality, 100% recommended to everyone. Buy it today without hesitation!",
                "rating", 5,
                "verified", false
        ));

        // Candidate 4: Genuine Review with Minor Criticism
        list.add(Map.of(
                "title", "Decent value for price",
                "content", "Packaging was slightly dented upon arrival, but the product inside works as advertised. Setup was easy and buttons are responsive. Satisfied for the price point.",
                "rating", 4,
                "verified", true
        ));

        // Candidate 5: Generic Short Praise
        list.add(Map.of(
                "title", "Love it",
                "content", "Super product works well very happy with purchase.",
                "rating", 5,
                "verified", false
        ));

        return list;
    }

    private String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
