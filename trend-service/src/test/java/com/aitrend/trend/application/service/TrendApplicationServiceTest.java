package com.aitrend.trend.application.service;

import com.aitrend.trend.application.port.in.CreateTrendCommand;
import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.exception.TrendNotFoundException;
import com.aitrend.trend.domain.model.SourceType;
import com.aitrend.trend.domain.model.Trend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrendApplicationServiceTest {

    @Mock
    private TrendRepositoryPort trendRepositoryPort;

    private TrendApplicationService trendApplicationService;

    @BeforeEach
    void setUp() {
        trendApplicationService = new TrendApplicationService(trendRepositoryPort);
    }

    @Test
    void shouldReturnPagedTrendsWhenGetTrendsCalled() {
        GetTrendsQuery query = new GetTrendsQuery(SourceType.GITHUB, null, null, 0, 10, "stars", "desc");
        Trend mockTrend = new Trend(1L, "ollama/ollama", "LLMs locally", "https://github.com/ollama/ollama",
                SourceType.GITHUB, 90000, 7000, "Go", List.of("llm"), 99.0, "AI", "Summary",
                LocalDateTime.now(), LocalDateTime.now());

        PagedResult<Trend> pagedResult = new PagedResult<>(List.of(mockTrend), 0, 10, 1L, 1, true);
        when(trendRepositoryPort.findTrends(query)).thenReturn(pagedResult);

        PagedResult<Trend> result = trendApplicationService.getTrends(query);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getTitle()).isEqualTo("ollama/ollama");
        verify(trendRepositoryPort).findTrends(query);
    }

    @Test
    void shouldReturnTrendByIdWhenFound() {
        Trend mockTrend = new Trend(1L, "ollama/ollama", "LLMs locally", "https://github.com/ollama/ollama",
                SourceType.GITHUB, 90000, 7000, "Go", List.of("llm"), 99.0, "AI", "Summary",
                LocalDateTime.now(), LocalDateTime.now());

        when(trendRepositoryPort.findById(1L)).thenReturn(Optional.of(mockTrend));

        Trend result = trendApplicationService.getTrendById(1L);

        assertThat(result.getTitle()).isEqualTo("ollama/ollama");
    }

    @Test
    void shouldThrowExceptionWhenTrendIdNotFound() {
        when(trendRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trendApplicationService.getTrendById(99L))
                .isInstanceOf(TrendNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldCreateTrendSuccessfully() {
        CreateTrendCommand command = new CreateTrendCommand("vllm-project/vllm", "Fast LLM serving",
                "https://github.com/vllm-project/vllm", SourceType.GITHUB, 30000, 4000, "Python",
                List.of("vllm"), 97.0, "Inference", "Engine");

        Trend savedTrend = new Trend(2L, command.title(), command.description(), command.repositoryUrl(),
                command.source(), command.stars(), command.forks(), command.language(), command.topics(),
                command.trendScore(), command.aiCategory(), command.aiSummary(), LocalDateTime.now(), LocalDateTime.now());

        when(trendRepositoryPort.save(any(Trend.class))).thenReturn(savedTrend);

        Trend result = trendApplicationService.createTrend(command);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("vllm-project/vllm");
    }
}
