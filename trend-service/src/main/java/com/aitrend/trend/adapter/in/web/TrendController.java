package com.aitrend.trend.adapter.in.web;

import com.aitrend.trend.adapter.in.web.dto.CreateTrendRequestDto;
import com.aitrend.trend.adapter.in.web.dto.TrendResponseDto;
import com.aitrend.trend.adapter.in.web.mapper.TrendWebMapper;
import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.in.TrendUseCase;
import com.aitrend.trend.domain.model.SourceType;
import com.aitrend.trend.domain.model.Trend;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trends")
@Tag(name = "Trends", description = "Aggregated AI repositories, open-weights models, trend scoring, search, and pagination.")
public class TrendController {

    private final TrendUseCase trendUseCase;

    public TrendController(TrendUseCase trendUseCase) {
        this.trendUseCase = trendUseCase;
    }

    @GetMapping
    @Operation(summary = "Get paginated, filtered, and searchable AI trends", description = "Query repositories and AI models across platforms like GitHub and Hugging Face with pagination and sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of AI trend items")
    })
    public ResponseEntity<PagedResult<TrendResponseDto>> getTrends(
            @Parameter(description = "Filter trends by platform source (GITHUB, HUGGING_FACE)")
            @RequestParam(required = false) SourceType source,

            @Parameter(description = "Filter by primary programming language (e.g. Python, Go)")
            @RequestParam(required = false) String language,

            @Parameter(description = "Search keyword matching title, description, or AI category")
            @RequestParam(required = false, name = "q") String searchKeyword,

            @Parameter(description = "Zero-based page index")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Field to sort by (stars, score, created, title)")
            @RequestParam(defaultValue = "stars") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)")
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
    @Operation(summary = "Get a single AI trend by ID", description = "Retrieves full details, topics, and AI metrics for a specific trend item.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trend details retrieved"),
            @ApiResponse(responseCode = "404", description = "Trend item not found")
    })
    public ResponseEntity<TrendResponseDto> getTrendById(
            @Parameter(description = "Unique database ID of the trend") @PathVariable Long id
    ) {
        Trend domain = trendUseCase.getTrendById(id);
        return ResponseEntity.ok(TrendWebMapper.toResponseDto(domain));
    }

    @PostMapping
    @Operation(summary = "Create or ingest a new AI trend item", description = "Saves a new AI trend item to the repository.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trend item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<TrendResponseDto> createTrend(@Valid @RequestBody CreateTrendRequestDto request) {
        Trend domain = trendUseCase.createTrend(TrendWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(TrendWebMapper.toResponseDto(domain));
    }
}
