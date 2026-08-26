package com.aitrend.analysis.domain.service;

import com.aitrend.analysis.domain.model.AiAnalysisResult;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RuleBasedCategorizerTest {

    private RuleBasedCategorizer categorizer;

    @BeforeEach
    void setUp() {
        categorizer = new RuleBasedCategorizer();
    }

    @Test
    void shouldCategorizeAgentProjectCorrectly() {
        TrendAnalysisRequest request = new TrendAnalysisRequest(
                1L, "crewAIInc/crewAI", "Framework for orchestrating autonomous AI agents",
                "https://github.com/crewAIInc/crewAI", "Python", List.of("agent", "crew"), 15000, 2000
        );

        AiAnalysisResult result = categorizer.generateFallbackMetadata(request);

        assertThat(result.category()).isEqualTo("Autonomous AI Agents & Orchestration");
        assertThat(result.summary()).contains("autonomous AI agents");
    }

    @Test
    void shouldCategorizeRagProjectCorrectly() {
        TrendAnalysisRequest request = new TrendAnalysisRequest(
                2L, "chroma-core/chroma", "Open-source embedding database for RAG retrieval search",
                "https://github.com/chroma-core/chroma", "Python", List.of("vector", "rag"), 12000, 1000
        );

        AiAnalysisResult result = categorizer.generateFallbackMetadata(request);

        assertThat(result.category()).isEqualTo("RAG & Vector Search Systems");
    }

    @Test
    void shouldCategorizeLocalInferenceProjectCorrectly() {
        TrendAnalysisRequest request = new TrendAnalysisRequest(
                3L, "ollama/ollama", "Get up and running with large language models locally",
                "https://github.com/ollama/ollama", "Go", List.of("ollama", "local", "llm"), 90000, 7000
        );

        AiAnalysisResult result = categorizer.generateFallbackMetadata(request);

        assertThat(result.category()).isEqualTo("Local LLM Execution & Inferencing");
    }
}
