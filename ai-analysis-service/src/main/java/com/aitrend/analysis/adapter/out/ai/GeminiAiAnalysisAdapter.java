package com.aitrend.analysis.adapter.out.ai;

import com.aitrend.analysis.application.port.out.GeminiAiPort;
import com.aitrend.analysis.domain.model.AiAnalysisResult;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;
import com.aitrend.analysis.domain.service.RuleBasedCategorizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiAiAnalysisAdapter implements GeminiAiPort {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiAnalysisAdapter.class);

    private final String apiKey;
    private final String model;
    private final RuleBasedCategorizer fallback;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiAiAnalysisAdapter(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-3.5-flash-lite}") String model,
            RuleBasedCategorizer fallback
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.fallback = fallback;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AiAnalysisResult analyzeTrend(TrendAnalysisRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("Gemini API key not configured. Using rule-based fallback categorizer for '{}'", request.title());
            return fallback.generateFallbackMetadata(request);
        }

        try {
            log.info("Calling Gemini API ({}) to enrich trend '{}'...", model, request.title());
            String promptText = String.format(
                    "You are an expert AI system cataloger. Analyze the following open-source project:\n" +
                    "Title: %s\nDescription: %s\nLanguage: %s\nTopics: %s\n\n" +
                    "Return ONLY a JSON object (no markdown formatting, no code blocks) with two keys:\n" +
                    "\"category\": One of [Local LLM Execution & Inferencing, Autonomous AI Agents & Orchestration, RAG & Vector Search Systems, Vision-Language & Multimodal AI, LLM Fine-Tuning & Quantization, AI & Machine Learning Frameworks]\n" +
                    "\"summary\": A concise 2-sentence executive summary of what this project does and why it is trending.",
                    request.title(), request.description(), request.language(), String.join(", ", request.topics())
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
                        return parseJsonMetadata(rawText, request);
                    }
                }
            }

            log.warn("Gemini API returned empty candidate response. Using fallback for '{}'", request.title());
            return fallback.generateFallbackMetadata(request);

        } catch (Exception e) {
            log.error("Gemini API call ({}) failed for '{}': {}. Using fallback.", model, request.title(), e.getMessage());
            return fallback.generateFallbackMetadata(request);
        }
    }

    private AiAnalysisResult parseJsonMetadata(String rawText, TrendAnalysisRequest request) {
        try {
            String cleanJson = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
            int firstBrace = cleanJson.indexOf("{");
            int lastBrace = cleanJson.lastIndexOf("}");
            if (firstBrace != -1 && lastBrace != -1) {
                cleanJson = cleanJson.substring(firstBrace, lastBrace + 1);
            }

            Map<String, String> map = objectMapper.readValue(cleanJson, Map.class);
            String category = map.getOrDefault("category", "AI & Machine Learning Frameworks");
            String summary = map.getOrDefault("summary", request.description());
            return new AiAnalysisResult(category, summary);
        } catch (Exception e) {
            log.warn("Failed to parse JSON response from Gemini API: {}. Raw: '{}'", e.getMessage(), rawText);
            return fallback.generateFallbackMetadata(request);
        }
    }
}
