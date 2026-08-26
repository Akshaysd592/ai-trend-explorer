package com.aitrend.analysis.domain.service;

import com.aitrend.analysis.domain.model.AiAnalysisResult;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resilient keyword-based fallback categorizer that produces structured
 * AI taxonomy categories when Google Gemini API is unconfigured or rate-limited.
 */
@Service
public class RuleBasedCategorizer {

    public AiAnalysisResult generateFallbackMetadata(TrendAnalysisRequest request) {
        String combined = (request.title() + " " +
                (request.description() != null ? request.description() : "") + " " +
                String.join(" ", request.topics()) + " " +
                (request.language() != null ? request.language() : "")).toLowerCase();

        String category;
        if (combined.contains("agent") || combined.contains("crew") || combined.contains("autogen") || combined.contains("langchain") || combined.contains("orchestrat")) {
            category = "Autonomous AI Agents & Orchestration";
        } else if (combined.contains("rag") || combined.contains("vector") || combined.contains("retrieval") || combined.contains("embedding") || combined.contains("search")) {
            category = "RAG & Vector Search Systems";
        } else if (combined.contains("vision") || combined.contains("image") || combined.contains("video") || combined.contains("audio") || combined.contains("multimodal") || combined.contains("diffusion")) {
            category = "Vision-Language & Multimodal AI";
        } else if (combined.contains("fine-tune") || combined.contains("quantiz") || combined.contains("lora") || combined.contains("gguf") || combined.contains("train")) {
            category = "LLM Fine-Tuning & Quantization";
        } else if (combined.contains("llama") || combined.contains("ollama") || combined.contains("vllm") || combined.contains("local") || combined.contains("inference") || combined.contains("serve")) {
            category = "Local LLM Execution & Inferencing";
        } else {
            category = "AI & Machine Learning Frameworks";
        }

        String summary = (request.description() != null && !request.description().isBlank())
                ? request.description()
                : String.format("A trending %s open-source artificial intelligence project with %d stars.",
                request.language() != null ? request.language() : "AI", request.stars() != null ? request.stars() : 0);

        return new AiAnalysisResult(category, summary);
    }
}
