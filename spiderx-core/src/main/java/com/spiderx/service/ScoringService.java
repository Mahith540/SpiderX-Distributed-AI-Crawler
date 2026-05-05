package com.spiderx.service;

public interface ScoringService {
    /**
     * Calculates a relevance score for a given text (e.g., anchor text or snippet).
     * @param text The text to analyze.
     * @param targetCategory The category or keywords to score against.
     * @return A score between 0.0 and 1.0.
     */
    double calculateScore(String text, String targetCategory);
}
