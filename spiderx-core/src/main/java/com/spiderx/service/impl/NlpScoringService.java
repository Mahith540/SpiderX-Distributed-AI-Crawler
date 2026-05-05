package com.spiderx.service.impl;

import com.spiderx.service.ScoringService;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NlpScoringService implements ScoringService {

    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    // A basic list of "noise" words (Stop words)
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "if", "then", "else", "at", "by", "from", "for", "in", "of", "on", "to", "with"
    ));

    @Override
    public double calculateScore(String text, String targetCategory) {
        if (text == null || text.isBlank() || targetCategory == null || targetCategory.isBlank()) {
            return 0.0;
        }

        // 1. Tokenize and normalize text
        Set<String> textTokens = tokenize(text);
        Set<String> targetTokens = tokenize(targetCategory);

        if (textTokens.isEmpty() || targetTokens.isEmpty()) {
            return 0.0;
        }

        // 2. Calculate Jaccard Similarity (Intersection over Union)
        long intersectionSize = textTokens.stream()
                .filter(targetTokens::contains)
                .count();

        double jaccardScore = (double) intersectionSize / (textTokens.size() + targetTokens.size() - intersectionSize);

        // 3. Weightage adjustment: exact matches in anchor text are high value
        // If the anchor text contains the exact category name, boost it
        if (text.toLowerCase().contains(targetCategory.toLowerCase())) {
            jaccardScore = Math.min(1.0, jaccardScore + 0.5);
        }

        return jaccardScore;
    }

    private Set<String> tokenize(String input) {
        return Arrays.stream(tokenizer.tokenize(input.toLowerCase()))
                .filter(token -> token.length() > 2) // Ignore very short words
                .filter(token -> !STOP_WORDS.contains(token)) // Filter stop words
                .collect(Collectors.toSet());
    }
}
