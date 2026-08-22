package com.aitrend.trend.adapter.in.web;

import com.aitrend.trend.adapter.in.web.dto.CreateTrendRequestDto;
import com.aitrend.trend.adapter.in.web.dto.TrendResponseDto;
import com.aitrend.trend.adapter.in.web.mapper.TrendWebMapper;
import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.in.TrendUseCase;
import com.aitrend.trend.domain.model.SourceType;
import com.aitrend.trend.domain.model.Trend;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trends")
@CrossOrigin(origins = "*")
public class TrendController {

    private final TrendUseCase trendUseCase;

    public TrendController(TrendUseCase trendUseCase) {
        this.trendUseCase = trendUseCase;
    }

    @GetMapping
    public ResponseEntity<PagedResult<TrendResponseDto>> getTrends(
            @RequestParam(required = false) SourceType source,
            @RequestParam(required = false) String language,
            @RequestParam(required = false, name = "q") String searchKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "stars") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        GetTrendsQuery query = new GetTrendsQuery(
                source, language, searchKeyword, page, size, sortBy, sortDir
        );
        PagedResult<Trend> domainResult = trendUseCase.getTrends(query);
        
        PagedResult<TrendResponseDto> dtoResult = new PagedResult<>(
                domainResult.content().stream().map(TrendWebMapper::toResponseDto).toList(),
                domainResult.pageNumber(),
                domainResult.pageSize(),
                domainResult.totalElements(),
                domainResult.totalPages(),
                domainResult.last()
        );
        
        return ResponseEntity.ok(dtoResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrendResponseDto> getTrendById(@PathVariable Long id) {
        Trend domain = trendUseCase.getTrendById(id);
        return ResponseEntity.ok(TrendWebMapper.toResponseDto(domain));
    }

    @PostMapping
    public ResponseEntity<TrendResponseDto> createTrend(@Valid @RequestBody CreateTrendRequestDto request) {
        Trend domain = trendUseCase.createTrend(TrendWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(TrendWebMapper.toResponseDto(domain));
    }
}
