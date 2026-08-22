package com.aitrend.trend.adapter.out.persistence;

import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.model.Trend;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TrendPersistenceAdapter implements TrendRepositoryPort {

    private final SpringDataTrendRepository repository;

    public TrendPersistenceAdapter(SpringDataTrendRepository repository) {
        this.repository = repository;
    }

    @Override
    public PagedResult<Trend> findTrends(GetTrendsQuery query) {
        Sort.Direction direction = "asc".equalsIgnoreCase(query.sortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;

        String sortProperty = switch (query.sortBy().toLowerCase()) {
            case "score", "trendscore" -> "trendScore";
            case "created", "createdat" -> "createdAt";
            case "title" -> "title";
            default -> "stars";
        };

        Pageable pageable = PageRequest.of(query.page(), query.size(), Sort.by(direction, sortProperty));

        Specification<TrendJpaEntity> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.source() != null) {
                predicates.add(cb.equal(root.get("source"), query.source()));
            }

            if (query.language() != null && !query.language().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("language")), query.language().toLowerCase()));
            }

            if (query.searchKeyword() != null && !query.searchKeyword().isBlank()) {
                String searchPattern = "%" + query.searchKeyword().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                Predicate categoryMatch = cb.like(cb.lower(root.get("aiCategory")), searchPattern);
                predicates.add(cb.or(titleMatch, descMatch, categoryMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<TrendJpaEntity> entityPage = repository.findAll(spec, pageable);

        List<Trend> domainContent = entityPage.getContent().stream()
                .map(this::toDomain)
                .toList();

        return new PagedResult<>(
                domainContent,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast()
        );
    }

    @Override
    public Optional<Trend> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Trend save(Trend trend) {
        TrendJpaEntity entity = toEntity(trend);
        TrendJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private Trend toDomain(TrendJpaEntity entity) {
        return new Trend(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getRepositoryUrl(),
                entity.getSource(),
                entity.getStars(),
                entity.getForks(),
                entity.getLanguage(),
                entity.getTopicsList(),
                entity.getTrendScore(),
                entity.getAiCategory(),
                entity.getAiSummary(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private TrendJpaEntity toEntity(Trend domain) {
        TrendJpaEntity entity = TrendJpaEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .repositoryUrl(domain.getRepositoryUrl())
                .source(domain.getSource())
                .stars(domain.getStars())
                .forks(domain.getForks())
                .language(domain.getLanguage())
                .trendScore(domain.getTrendScore())
                .aiCategory(domain.getAiCategory())
                .aiSummary(domain.getAiSummary())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        entity.setTopicsList(domain.getTopics());
        return entity;
    }
}
