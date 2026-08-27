package com.reviewsentinel.analyzer;

import com.reviewsentinel.model.AnalysisResult;
import com.reviewsentinel.model.PhraseHighlight;
import com.reviewsentinel.model.SignalReason;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReviewAnalyzer {

    // Promotional & hyper-enthusiastic keywords/phrases
    private static final List<String> PROMOTIONAL_PHRASES = Arrays.asList(
            "amazing amazing", "best product ever", "must buy", "100%", "100% perfect",
            "life changing", "highly recommend", "game changer", "buy it now", "dont hesitate",
            "godsend", "miracle product", "mind blowing", "flawless", "unbelievable",
            "top notch", "five stars", "5 stars", "best thing ever", "absolutely love",
            "greatest ever", "perfection", "guaranteed", "best purchase ever"
    );

    private static final List<String> PROMOTIONAL_WORDS = Arrays.asList(
            "amazing", "best", "perfect", "awesome", "incredible", "excellent",
            "unbelievable", "mindblowing", "spectacular", "flawless", "miracle", "outstanding"
    );

    private static final List<String> SPECIFICITY_KEYWORDS = Arrays.asList(
            "battery", "sound", "noise", "bass", "cable", "material", "plastic", "metal",
            "hours", "days", "weeks", "months", "build quality", "fit", "comfort", "ear",
            "head", "cushion", "mic", "microphone", "call", "bluetooth", "range", "volume",
            "weight", "heavy", "light", "setup", "instruction", "manual", "box", "packaging",
            "button", "charging", "case", "connector", "warranty", "price", "cost", "value"
    );

    private static final List<String> GENERIC_STATEMENTS = Arrays.asList(
            "great product", "love it", "works well", "good item", "nice product",
            "very good", "super product", "happy with purchase", "satisfied", "worth it"
    );

    public AnalysisResult analyze(String productName, Integer rating, String reviewTitle, String reviewContent, Boolean verifiedPurchase) {
        if (reviewContent == null) {
            reviewContent = "";
        }
        if (reviewTitle == null) {
            reviewTitle = "";
        }
        if (rating == null) {
            rating = 5;
        }
        if (verifiedPurchase == null) {
            verifiedPurchase = false;
        }

        String fullText = (reviewTitle + " " + reviewContent).trim();
        String lowerText = fullText.toLowerCase();

        int riskScore = 0;
        List<SignalReason> signals = new ArrayList<>();
        List<PhraseHighlight> highlights = new ArrayList<>();
        Set<String> processedPhrases = new HashSet<>();

        // Rule 1: Excessive Punctuation
        long exclamationCount = fullText.chars().filter(ch -> ch == '!').count();
        if (exclamationCount >= 3) {
            int pts = (exclamationCount >= 6) ? 20 : 15;
            riskScore += pts;
            signals.add(new SignalReason(
                    "Excessive Punctuation",
                    "The review contains an unusually high number of exclamation marks (" + exclamationCount + ").",
                    "⚠",
                    pts
            ));
        }

        // Rule 2: Excessive Capitalization (SHOUTING)
        long totalLetters = fullText.chars().filter(Character::isLetter).count();
        long upperLetters = fullText.chars().filter(Character::isUpperCase).count();
        double capitalRatio = totalLetters > 0 ? (double) upperLetters / totalLetters : 0;

        if (totalLetters > 15 && capitalRatio > 0.35) {
            int pts = 12;
            riskScore += pts;
            signals.add(new SignalReason(
                    "Excessive Capitalization",
                    String.format("High proportion of uppercase letters (%.0f%%), signaling artificial excitement or shouting.", capitalRatio * 100),
                    "⚡",
                    pts
            ));
        }

        // Rule 3: Repeated Words & Consecutive Repetition
        Pattern repeatPattern = Pattern.compile("(?i)\\b(\\w+)\\s+\\1\\b");
        Matcher repeatMatcher = repeatPattern.matcher(fullText);
        int repeatCount = 0;
        while (repeatMatcher.find()) {
            repeatCount++;
            String matchedWord = repeatMatcher.group(0);
            if (!processedPhrases.contains(matchedWord.toLowerCase())) {
                highlights.add(new PhraseHighlight(matchedWord, "Repeated words detected", 18));
                processedPhrases.add(matchedWord.toLowerCase());
            }
        }
        if (repeatCount > 0) {
            int pts = Math.min(25, repeatCount * 15);
            riskScore += pts;
            signals.add(new SignalReason(
                    "Repetition Detected",
                    "Repeated words and phrases reduce review authenticity and indicate bot-like patterns.",
                    "⚠",
                    pts
            ));
        }

        // Rule 4: Promotional Language & Superlatives
        int promoMatches = 0;
        for (String phrase : PROMOTIONAL_PHRASES) {
            if (lowerText.contains(phrase)) {
                promoMatches++;
                if (!processedPhrases.contains(phrase)) {
                    highlights.add(new PhraseHighlight(phrase, "Promotional hyperbole detected", 15));
                    processedPhrases.add(phrase);
                }
            }
        }

        for (String word : PROMOTIONAL_WORDS) {
            Pattern wordPattern = Pattern.compile("(?i)\\b" + Pattern.quote(word) + "\\b");
            Matcher m = wordPattern.matcher(fullText);
            int count = 0;
            while (m.find()) {
                count++;
            }
            if (count >= 2) {
                promoMatches++;
                if (!processedPhrases.contains(word.toLowerCase())) {
                    highlights.add(new PhraseHighlight(word, "Overused superlative word", 12));
                    processedPhrases.add(word.toLowerCase());
                }
            }
        }

        if (promoMatches > 0) {
            int pts = Math.min(25, promoMatches * 12);
            riskScore += pts;
            signals.add(new SignalReason(
                    "Promotional Language",
                    "Strong marketing-style phrases and non-neutral hype were detected.",
                    "📢",
                    pts
            ));
        }

        // Rule 5: Generic Praise & Low Specificity
        long specCount = SPECIFICITY_KEYWORDS.stream().filter(lowerText::contains).count();
        int wordCount = fullText.split("\\s+").length;

        if (wordCount < 12) {
            int pts = 10;
            riskScore += pts;
            signals.add(new SignalReason(
                    "Very Short Review",
                    "Short, non-detailed reviews carry higher risk of automated submission.",
                    "🔍",
                    pts
            ));
        } else if (specCount == 0 && wordCount < 30 && rating == 5) {
            int pts = 12;
            riskScore += pts;
            signals.add(new SignalReason(
                    "Low Review Specificity",
                    "Lacks concrete details or specific product feature mentions.",
                    "🎯",
                    pts
            ));
        }

        for (String gen : GENERIC_STATEMENTS) {
            if (lowerText.contains(gen) && specCount == 0) {
                if (!processedPhrases.contains(gen)) {
                    highlights.add(new PhraseHighlight(gen, "Generic non-specific claim", 10));
                    processedPhrases.add(gen);
                }
            }
        }

        // Rule 6: Unrealistic Sentiment & Rating Synergy
        if (rating == 5 && promoMatches >= 2 && exclamationCount >= 2) {
            int pts = 10;
            riskScore += pts;
            signals.add(new SignalReason(
                    "Unrealistic Positive Sentiment",
                    "Over-the-top positive sentiment disproportionate to standard user feedback.",
                    "🌟",
                    pts
            ));
        }

        // Rule 7: Unverified Purchase Risk Weight
        if (!verifiedPurchase && riskScore > 15) {
            int pts = 8;
            riskScore += pts;
            signals.add(new SignalReason(
                    "Unverified Buyer Profile",
                    "Review originates from an unverified purchase account with suspicious signals.",
                    "🏷️",
                    pts
            ));
        }

        // Clamp Risk Score 0-99
        riskScore = Math.min(99, Math.max(0, riskScore));
        double fakeProbability = riskScore;

        // Determine Verdict & Risk Level
        String verdict;
        String verdictIcon;
        String riskLevel;

        if (riskScore <= 35) {
            verdict = "LIKELY GENUINE";
            verdictIcon = "✓";
            riskLevel = "LOW";
        } else if (riskScore <= 65) {
            verdict = "SUSPICIOUS";
            verdictIcon = "◈";
            riskLevel = "MEDIUM";
        } else {
            verdict = "LIKELY FAKE";
            verdictIcon = "⚠";
            riskLevel = "HIGH";
        }

        // Determine Sentiment
        String sentiment = calculateSentiment(lowerText, rating, promoMatches);

        // Calculate Confidence (78% to 98% based on sample size and signal count)
        double confidence = Math.min(98.0, Math.max(78.0, 82.0 + (signals.size() * 3.5) + (wordCount > 15 ? 4.0 : 0.0)));

        // Calculate Quality Score (100 - riskScore + specificityBonus)
        int qualityScore = (int) Math.min(100, Math.max(10, (100 - riskScore * 0.7) + (specCount * 5)));

        // Build Highlighted HTML
        String highlightedHtml = buildHighlightedHtml(fullText, highlights);

        // Assemble AnalysisResult DTO
        AnalysisResult result = new AnalysisResult();
        result.setProductName(productName);
        result.setRating(rating);
        result.setReviewTitle(reviewTitle);
        result.setOriginalText(fullText);
        result.setVerifiedPurchase(verifiedPurchase);
        result.setFakeProbability(fakeProbability);
        result.setConfidence(confidence);
        result.setReviewQuality(qualityScore);
        result.setSentiment(sentiment);
        result.setRiskLevel(riskLevel);
        result.setVerdict(verdict);
        result.setVerdictIcon(verdictIcon);
        result.setSignals(signals);
        result.setHighlightedPhrases(highlights);
        result.setHighlightedHtml(highlightedHtml);

        return result;
    }

    private String calculateSentiment(String lowerText, int rating, int promoMatches) {
        if (lowerText.contains("terrible") || lowerText.contains("worst") || lowerText.contains("horrible") || lowerText.contains("useless") || lowerText.contains("waste of money")) {
            return "VERY NEGATIVE";
        } else if (lowerText.contains("bad") || lowerText.contains("poor") || lowerText.contains("disappointed") || rating <= 2) {
            return "NEGATIVE";
        } else if (promoMatches >= 2 || lowerText.contains("amazing") || lowerText.contains("best") || rating == 5) {
            return "VERY POSITIVE";
        } else if (rating == 4 || lowerText.contains("good") || lowerText.contains("decent") || lowerText.contains("satisfied")) {
            return "POSITIVE";
        }
        return "NEUTRAL";
    }

    private String buildHighlightedHtml(String text, List<PhraseHighlight> highlights) {
        if (highlights == null || highlights.isEmpty()) {
            return escapeHtml(text);
        }

        // Sort highlights by length descending to avoid nested replacement collision
        List<PhraseHighlight> sorted = new ArrayList<>(highlights);
        sorted.sort((a, b) -> Integer.compare(b.getPhrase().length(), a.getPhrase().length()));

        String html = escapeHtml(text);
        for (PhraseHighlight ph : sorted) {
            String phrase = ph.getPhrase();
            String escapedPhrase = escapeHtml(phrase);
            Pattern p = Pattern.compile("(?i)\\b" + Pattern.quote(escapedPhrase) + "\\b");
            Matcher m = p.matcher(html);
            if (m.find()) {
                String replacement = String.format(
                        "<mark class=\"suspicious-highlight\" data-tooltip=\"%s (Risk contribution: +%d)\">$0</mark>",
                        escapeHtml(ph.getReason()), ph.getRiskScore()
                );
                html = m.replaceAll(replacement);
            }
        }
        return html;
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
