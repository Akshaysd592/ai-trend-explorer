package com.aitrend.trend.adapter.out.github;

import com.aitrend.trend.application.port.out.FetchGitHubTrendsPort;
import com.aitrend.trend.domain.model.SourceType;
import com.aitrend.trend.domain.model.Trend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GitHubApiClientAdapter implements FetchGitHubTrendsPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubApiClientAdapter.class);
    private final RestClient restClient;

    public GitHubApiClientAdapter(@Value("${github.token:${GITHUB_TOKEN:}}") String githubToken) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("User-Agent", "AI-Trend-Explorer-Service")
                .defaultHeader("Accept", "application/vnd.github.v3+json");

        if (githubToken != null && !githubToken.isBlank()) {
            log.info("Configuring GitHubApiClientAdapter with authenticated Bearer token (5000 req/hr rate limit)...");
            builder.defaultHeader("Authorization", "Bearer " + githubToken);
        } else {
            log.warn("No GITHUB_TOKEN provided. Operating under unauthenticated GitHub API rate limits (10 req/min).");
        }

        this.restClient = builder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Trend> fetchTrendingRepositories() {
        log.info("Fetching trending AI repositories from GitHub REST API...");
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/search/repositories?q=topic:ai+OR+topic:llm+OR+topic:machine-learning+OR+topic:deep-learning&sort=stars&order=desc&per_page=25")
                    .retrieve()
                    .body(Map.class);

            if (response == null || !response.containsKey("items")) {
                log.warn("GitHub API returned empty response body");
                return List.of();
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            List<Trend> trends = new ArrayList<>();

            for (Map<String, Object> item : items) {
                String fullPath = (String) item.get("full_name");
                String description = (String) item.get("description");
                String htmlUrl = (String) item.get("html_url");
                String language = (String) item.get("language");
                Integer stargazersCount = item.get("stargazers_count") != null ? ((Number) item.get("stargazers_count")).intValue() : 0;
                Integer forksCount = item.get("forks_count") != null ? ((Number) item.get("forks_count")).intValue() : 0;
                List<String> topics = item.get("topics") != null ? (List<String>) item.get("topics") : List.of();

                Trend trend = new Trend(
                        null,
                        fullPath != null ? fullPath : "unknown/repo",
                        description != null ? description : "AI Repository",
                        htmlUrl != null ? htmlUrl : "https://github.com",
                        SourceType.GITHUB,
                        stargazersCount,
                        forksCount,
                        language != null ? language : "Python",
                        topics,
                        0.0,
                        "AI & Machine Learning",
                        description,
                        null,
                        null
                );
                trends.add(trend);
            }
            log.info("Successfully fetched {} trending repositories from GitHub", trends.size());
            return trends;
        } catch (Exception e) {
            log.error("Failed to fetch trends from GitHub API: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
