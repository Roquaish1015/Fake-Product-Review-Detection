package com.reviewsentinel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer rating;

    private String reviewTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reviewContent;

    private Boolean verifiedPurchase;

    private Double fakeProbability;
    private Double confidence;
    private Integer reviewQuality;
    private String sentiment;
    private String riskLevel;
    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String detectedSignalsJson;

    @Column(columnDefinition = "TEXT")
    private String phraseBreakdownsJson;

    private LocalDateTime createdAt;

    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(String productName, Integer rating, String reviewTitle, String reviewContent, Boolean verifiedPurchase) {
        this.productName = productName;
        this.rating = rating;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.verifiedPurchase = verifiedPurchase;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase != null ? verifiedPurchase : false;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public Double getFakeProbability() {
        return fakeProbability;
    }

    public void setFakeProbability(Double fakeProbability) {
        this.fakeProbability = fakeProbability;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Integer getReviewQuality() {
        return reviewQuality;
    }

    public void setReviewQuality(Integer reviewQuality) {
        this.reviewQuality = reviewQuality;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getDetectedSignalsJson() {
        return detectedSignalsJson;
    }

    public void setDetectedSignalsJson(String detectedSignalsJson) {
        this.detectedSignalsJson = detectedSignalsJson;
    }

    public String getPhraseBreakdownsJson() {
        return phraseBreakdownsJson;
    }

    public void setPhraseBreakdownsJson(String phraseBreakdownsJson) {
        this.phraseBreakdownsJson = phraseBreakdownsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
