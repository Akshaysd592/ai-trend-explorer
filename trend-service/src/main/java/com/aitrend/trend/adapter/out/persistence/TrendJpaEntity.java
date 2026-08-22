package com.aitrend.trend.adapter.out.persistence;

import com.aitrend.trend.domain.model.SourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "trends")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType source;

    private Integer stars;
    private Integer forks;
    private String language;

    @Column(name = "topics_csv", columnDefinition = "TTEX")
    private String topicsCsv;

    @Column(name = "trend_score")
    private Double trendScore;

    @Column(name = "ai_category")
    private String aiCategory;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public List<String> getTopicsList() {
        if (topicsCsv == null || topicsCsv.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.asList(topicsCsv.split(","));
    }

    public void setTopicsList(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            this.topicsCsv = "";
        } else {
            this.topicsCsv = String.join(",", topics);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
