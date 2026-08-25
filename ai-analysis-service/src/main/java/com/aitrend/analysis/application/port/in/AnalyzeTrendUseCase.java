package com.aitrend.analysis.application.port.in;

import com.aitrend.analysis.domain.model.AiAnalysisResult;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;

public interface AnalyzeTrendUseCase {
    AiAnalysisResult processAndDispatchAnalysis(TrendAnalysisRequest request);
}
