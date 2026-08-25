package com.aitrend.analysis.application.port.out;

import com.aitrend.analysis.domain.model.AiAnalysisResult;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;

public interface GeminiAiPort {
    AiAnalysisResult analyzeTrend(TrendAnalysisRequest request);
}
