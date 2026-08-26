package com.aitrend.trend.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Trend {
    private final Long id;
    private final String title;
    private final String description;
    private final String repositoryUrl;
    private final SourceType source;
    private final Integer stars;
    private final Integer forks;
    private final String language;
    private final List<String> topics;
    private final Double trendScore;
    private final String aiCategory;
    private final String aiSummary;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @JsonCreator
    public Trend(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("repositoryUrl") String repositoryUrl,
            @JsonProperty("source") SourceType source,
            @JsonProperty("stars") Integer stars,
            @JsonProperty("forks") Integer forks,
            @JsonProperty("language") String language,
            @JsonProperty("topics") List<String> topics,
            @JsonProperty("trendScore") Double trendScore,
            @JsonProperty("aiCategory") String aiCategory,
            @JsonProperty("aiSummary") String aiSummary,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt
    ) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = description;
        this.repositoryUrl = repositoryUrl;
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.stars = stars != null ? stars : 0;
        this.forks = forks != null ? forks : 0;
        this.language = language;
        this.topics = topics != null ? topics : List.of();
        this.trendScore = trendScore != null ? trendScore : 0.0;
        this.aiCategory = aiCategory;
        this.aiSummary = aiSummary;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRepositoryUrl() { return repositoryUrl; }
    public SourceType getSource() { return source; }
    public Integer getStars() { return stars; }
    public Integer getForks() { return forks; }
    public String getLanguage() { return language; }
    public List<String> getTopics() { return topics; }
    public Double getTrendScore() { return trendScore; }
    public String getAiCategory() { return aiCategory; }
    public String getAiSummary() { return aiSummary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trend trend = (Trend) o;
        return Objects.equals(id, trend.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
