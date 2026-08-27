package com.reviewsentinel.model;

public class SignalReason {
    private String title;
    private String description;
    private String icon; // e.g. "⚠", "⚡", "🔍"
    private int riskPoints;

    public SignalReason() {}

    public SignalReason(String title, String description, String icon, int riskPoints) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.riskPoints = riskPoints;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getRiskPoints() {
        return riskPoints;
    }

    public void setRiskPoints(int riskPoints) {
        this.riskPoints = riskPoints;
    }
}
