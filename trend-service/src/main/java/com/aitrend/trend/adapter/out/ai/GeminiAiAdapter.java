package com.aitrend.trend.adapter.out.ai;

import com.aitrend.trend.application.port.out.AiEnrichmentPort;
import com.aitrend.trend.domain.model.AiMetadata;
import com.aitrend.trend.domain.model.Trend;
import com.aitrend.trend.domain.service.RuleBasedCategoryFallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiAiAdapter implements AiEnrichmentPort {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiAdapter.class);

    private final String apiKey;
    private final String model;
    private final RuleBasedCategoryFallback fallback;
    private final RestClient restClient;

    public GeminiAiAdapter(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-3.5-flash-lite}") String model,
            RuleBasedCategoryFallback fallback
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.fallback = fallback;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AiMetadata enrich(Trend trend) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("Gemini API key not configured. Using rule-based fallback categorizer for '{}'", trend.getTitle());
            return fallback.generateFallbackMetadata(trend);
        }

        try {
            log.info("Calling Gemini API ({}) to enrich trend '{}'...", model, trend.getTitle());
            String promptText = String.format(
                    "You are an expert AI system cataloger. Analyze the following open-source project:\n" +
                    "Title: %s\nDescription: %s\nLanguage: %s\nTopics: %s\n\n" +
                    "Return ONLY a JSON object (no markdown formatting, no code blocks) with two keys:\n" +
                    "\"category\": One of [Local LLM Execution & Inferencing, Autonomous AI Agents & Orchestration, RAG & Vector Search Systems, Vision-Language & Multimodal AI, LLM Fine-Tuning & Quantization, AI & Machine Learning Frameworks]\n" +
                    "\"summary\": A concise 2-sentence executive summary of what this project does and why it is trending.",
                    trend.getTitle(), trend.getDescription(), trend.getLanguage(), String.join(", ", trend.getTopics())
            );

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", promptText)))
                    )
            );

            Map<String, Object> response = restClient.post()
                    .uri("/v1beta/models/" + model + ":generateContent?key=" + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        String rawText = (String) parts.get(0).get("text");
                        return parseJsonMetadata(rawText, trend);
                    }
                }
            }

            log.warn("Gemini API response parsing returned empty candidate body. Using fallback.");
            return fallback.generateFallbackMetadata(trend);
        } catch (Exception e) {
            log.error("Gemini API call ({}) failed for '{}': {}. Falling back to rule-based categorizer.", model, trend.getTitle(), e.getMessage());
            return fallback.generateFallbackMetadata(trend);
        }
    }

    private AiMetadata parseJsonMetadata(String rawText, Trend trend) {
        try {
            String cleanJson = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
            int firstBrace = cleanJson.indexOf("{");
            int lastBrace = cleanJson.lastIndexOf("}");
            if (firstBrace != -1 && lastBrace != -1) {
                cleanJson = cleanJson.substring(firstBrace, lastBrace + 1);
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, String> map = mapper.readValue(cleanJson, Map.class);
            String category = map.getOrDefault("category", "AI & Machine Learning Frameworks");
            String summary = map.getOrDefault("summary", trend.getDescription());
            return new AiMetadata(category, summary);
        } catch (Exception e) {
            log.warn("Failed to parse JSON response from Gemini API: {}. Raw: '{}'", e.getMessage(), rawText);
            return fallback.generateFallbackMetadata(trend);
        }
    }
}
