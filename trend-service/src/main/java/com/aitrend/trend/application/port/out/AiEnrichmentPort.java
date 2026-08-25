package com.aitrend.trend.application.port.out;

import com.aitrend.trend.domain.model.AiMetadata;
import com.aitrend.trend.domain.model.Trend;

public interface AiEnrichmentPort {
    AiMetadata enrich(Trend trend);
}
