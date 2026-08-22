package com.aitrend.trend.application.port.in;

import java.time.Instant;

public record IngestionResult(
        int totalIngested,
        int githubCount,
        int huggingFaceCount,
        Instant timestamp
) {}
