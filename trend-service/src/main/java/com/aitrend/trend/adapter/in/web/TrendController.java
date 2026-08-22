package com.aitrend.trend.adapter.in.web;

import com.aitrend.trend.application.port.in.CreateTrendCommand;
import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.in.TrendUseCase;
import com.aitrend.trend.domain.model.Trend;
import com.aitrend.trend.infrastructure.openapi.api.TrendsApi;
import com.aitrend.trend.infrastructure.openapi.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trends")
public class TrendController implements TrendsApi {

    private final TrendUseCase trendUseCase;

    public TrendController(TrendUseCase trendUseCase) {
        this.trendUseCase = trendUseCase;
    }

    @Override
    @GetMapping
    public ResponseEntity<PagedResultTrendResponseDto> getTrends(
            @RequestParam(required = false) SourceType source,
            @RequestParam(required = false) String language,
            @RequestParam(required = false, name = "q") String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "stars") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        com.aitrend.trend.domain.model.SourceType domainSource = source != null ?
                com.aitrend.trend.domain.model.SourceType.valueOf(source.getValue()) : null;

        GetTrendsQuery query = new GetTrendsQuery(
                domainSource, language, q, page != null ? page : 0, size != null ? size : 20, sortBy, sortDir
        );
        PagedResult<Trend> domainResult = trendUseCase.getTrends(query);

        List<TrendResponseDto> dtos = domainResult.content().stream().map(this::toResponseDto).toList();

        PagedResultTrendResponseDto result = new PagedResultTrendResponseDto()
                .content(dtos)
                .pageNumber(domainResult.pageNumber())
                .pageSize(domainResult.pageSize())
                .totalElements(domainResult.totalElements())
                .totalPages(domainResult.totalPages())
                .last(domainResult.last());

        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TrendResponseDto> getTrendById(@PathVariable Long id) {
        Trend domain = trendUseCase.getTrendById(id);
        return ResponseEntity.ok(toResponseDto(domain));
    }

    @Override
    @PostMapping
    public ResponseEntity<TrendResponseDto> createTrend(@Valid @RequestBody CreateTrendRequestDto request) {
        com.aitrend.trend.domain.model.SourceType domainSource = request.getSource() != null ?
                com.aitrend.trend.domain.model.SourceType.valueOf(request.getSource().getValue()) : null;

        CreateTrendCommand command = new CreateTrendCommand(
                request.getTitle(),
                request.getDescription(),
                request.getRepositoryUrl(),
                domainSource,
                request.getStars(),
                request.getForks(),
                request.getLanguage(),
                request.getTopics(),
                request.getTrendScore(),
                request.getAiCategory(),
                request.getAiSummary()
        );
        Trend domain = trendUseCase.createTrend(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(domain));
    }

    private TrendResponseDto toResponseDto(Trend trend) {
        return new TrendResponseDto()
                .id(trend.getId())
                .title(trend.getTitle())
                .description(trend.getDescription())
                .repositoryUrl(trend.getRepositoryUrl())
                .source(trend.getSource() != null ? SourceType.fromValue(trend.getSource().name()) : null)
                .stars(trend.getStars())
                .forks(trend.getForks())
                .language(trend.getLanguage())
                .topics(trend.getTopics())
                .trendScore(trend.getTrendScore())
                .aiCategory(trend.getAiCategory())
                .aiSummary(trend.getAiSummary());
    }
}
