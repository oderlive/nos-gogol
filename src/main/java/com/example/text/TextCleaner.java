package com.example.text;
import com.example.config.AppConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TextCleaner implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Set<String> stopwords;

    public TextCleaner() {
        this.stopwords = loadStopwords();
    }
    private Set<String> loadStopwords() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(AppConfig.STOPWORDS_RESOURCE)) {
            if (is == null) return new HashSet<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public String[] cleanAndTokenize(String line) {
        if (line == null || line.isBlank()) return new String[0];

        String normalized = line.toLowerCase()
                .replaceAll("[^а-яёa-z\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.isEmpty()) return new String[0];

        return Arrays.stream(normalized.split(" "))
                .filter(w -> w.length() >= AppConfig.MIN_WORD_LENGTH)
                .filter(w -> !stopwords.contains(w))
                .toArray(String[]::new);
    }
}