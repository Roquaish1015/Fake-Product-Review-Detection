package com.reviewsentinel.model;

import java.util.List;

public class AnalysisResult {
    private Long reviewId;
    private String productName;
    private Integer rating;
    private String reviewTitle;
    private String originalText;
    private Boolean verifiedPurchase;

    private double fakeProbability; // 0 to 100
    private double confidence;      // 0 to 100
    private int reviewQuality;      // 0 to 100
    private String sentiment;       // VERY POSITIVE, POSITIVE, NEUTRAL, NEGATIVE, VERY NEGATIVE
    private String riskLevel;       // LOW, MEDIUM, HIGH
    private String verdict;         // LIKELY GENUINE, SUSPICIOUS, LIKELY FAKE
    private String verdictIcon;     // ✓, ◈, ⚠

    private List<SignalReason> signals;
    private List<PhraseHighlight> highlightedPhrases;
    private String highlightedHtml;

    public AnalysisResult() {}

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
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

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public double getFakeProbability() {
        return fakeProbability;
    }

    public void setFakeProbability(double fakeProbability) {
        this.fakeProbability = fakeProbability;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getReviewQuality() {
        return reviewQuality;
    }

    public void setReviewQuality(int reviewQuality) {
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

    public String getVerdictIcon() {
        return verdictIcon;
    }

    public void setVerdictIcon(String verdictIcon) {
        this.verdictIcon = verdictIcon;
    }

    public List<SignalReason> getSignals() {
        return signals;
    }

    public void setSignals(List<SignalReason> signals) {
        this.signals = signals;
    }

    public List<PhraseHighlight> getHighlightedPhrases() {
        return highlightedPhrases;
    }

    public void setHighlightedPhrases(List<PhraseHighlight> highlightedPhrases) {
        this.highlightedPhrases = highlightedPhrases;
    }

    public String getHighlightedHtml() {
        return highlightedHtml;
    }

    public void setHighlightedHtml(String highlightedHtml) {
        this.highlightedHtml = highlightedHtml;
    }
}
