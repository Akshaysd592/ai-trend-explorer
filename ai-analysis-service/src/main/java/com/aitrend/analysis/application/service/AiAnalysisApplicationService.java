package com.aitrend.analysis.application.service;

import com.aitrend.analysis.application.port.in.AnalyzeTrendUseCase;
import com.aitrend.analysis.application.port.out.GeminiAiPort;
import com.aitrend.analysis.application.port.out.TrendServiceClientPort;
import com.aitrend.analysis.domain.model.AiAnalysisResult;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AiAnalysisApplicationService implements AnalyzeTrendUseCase {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisApplicationService.class);

    private final GeminiAiPort geminiAiPort;
    private final TrendServiceClientPort trendServiceClientPort;

    public AiAnalysisApplicationService(
            GeminiAiPort geminiAiPort,
            TrendServiceClientPort trendServiceClientPort
    ) {
        this.geminiAiPort = geminiAiPort;
        this.trendServiceClientPort = trendServiceClientPort;
    }

    @Override
    public AiAnalysisResult processAndDispatchAnalysis(TrendAnalysisRequest request) {
        log.info("Processing AI enrichment analysis for trend id: {}, title: '{}'",
                request.trendId(), request.title());

        AiAnalysisResult analysisResult = geminiAiPort.analyzeTrend(request);

        if (request.trendId() != null) {
            log.info("Dispatching HTTP PATCH update to trend-service for trend id: {} (Category: '{}')",
                    request.trendId(), analysisResult.category());
            trendServiceClientPort.updateTrendAiMetadata(request.trendId(), analysisResult);
        }

        return analysisResult;
    }
}
