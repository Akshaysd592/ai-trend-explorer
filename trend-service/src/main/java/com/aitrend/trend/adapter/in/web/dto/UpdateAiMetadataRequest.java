package com.aitrend.trend.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAiMetadataRequest(
        @NotBlank(message = "aiCategory cannot be blank")
        String aiCategory,

        @NotBlank(message = "aiSummary cannot be blank")
        String aiSummary
) {
}
