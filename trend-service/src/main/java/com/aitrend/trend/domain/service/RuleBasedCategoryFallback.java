package com.aitrend.trend.domain.service;

import com.aitrend.trend.domain.model.AiMetadata;
import com.aitrend.trend.domain.model.Trend;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleBasedCategoryFallback {

    public AiMetadata generateFallbackMetadata(Trend trend) {
        String title = trend.getTitle() != null ? trend.getTitle().toLowerCase() : "";
        String desc = trend.getDescription() != null ? trend.getDescription().toLowerCase() : "";
        List<String> topics = trend.getTopics() != null ? trend.getTopics().stream().map(String::toLowerCase).toList() : List.of();

        String combined = title + " " + desc + " " + String.join(" ", topics);

        String category;
        if (combined.contains("local") || combined.contains("ollama") || combined.contains("llama.cpp") || combined.contains("vllm") || combined.contains("inference")) {
            category = "Local LLM Execution & Inferencing";
        } else if (combined.contains("agent") || combined.contains("crewai") || combined.contains("autogen") || combined.contains("langchain")) {
            category = "Autonomous AI Agents & Orchestration";
        } else if (combined.contains("rag") || combined.contains("vector") || combined.contains("embedding") || combined.contains("chroma") || combined.contains("qdrant")) {
            category = "RAG & Vector Search Systems";
        } else if (combined.contains("vision") || combined.contains("diffusers") || combined.contains("whisper") || combined.contains("multimodal") || combined.contains("image")) {
            category = "Vision-Language & Multimodal AI";
        } else if (combined.contains("fine-tune") || combined.contains("quantiz") || combined.contains("gguf") || combined.contains("lora")) {
            category = "LLM Fine-Tuning & Quantization";
        } else {
            category = "AI & Machine Learning Frameworks";
        }

        String summary = (trend.getDescription() != null && !trend.getDescription().isBlank())
                ? trend.getDescription()
                : "High-velocity AI project " + trend.getTitle() + " trending on " + trend.getSource() + ".";

        return new AiMetadata(category, summary);
    }
}
