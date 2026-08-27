package com.reviewsentinel.model;

public class PhraseHighlight {
    private String phrase;
    private String reason;
    private int riskScore;

    public PhraseHighlight() {}

    public PhraseHighlight(String phrase, String reason, int riskScore) {
        this.phrase = phrase;
        this.reason = reason;
        this.riskScore = riskScore;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }
}
