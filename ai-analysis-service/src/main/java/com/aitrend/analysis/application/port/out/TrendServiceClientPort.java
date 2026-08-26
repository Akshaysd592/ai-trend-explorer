package com.aitrend.analysis.application.port.out;

import com.aitrend.analysis.domain.model.AiAnalysisResult;

public interface TrendServiceClientPort {
    void updateTrendAiMetadata(Long trendId, AiAnalysisResult result);
}
