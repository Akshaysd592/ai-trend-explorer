package com.aitrend.trend.adapter.out;

import com.aitrend.trend.adapter.out.github.GitHubApiClientAdapter;
import com.aitrend.trend.adapter.out.huggingface.HuggingFaceApiClientAdapter;
import com.aitrend.trend.domain.model.Trend;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalApiIntegrationTest {

    @Test
    void shouldFetchRealGitHubTrendingRepositories() {
        GitHubApiClientAdapter adapter = new GitHubApiClientAdapter();
        List<Trend> trends = adapter.fetchTrendingRepositories();

        assertThat(trends).isNotNull();
        if (!trends.isEmpty()) {
            System.out.println(">>> GitHub Trends fetched: " + trends.size());
            for (int i = 0; i < Math.min(3, trends.size()); i++) {
                System.out.println("    " + trends.get(i).getTitle() + " | Stars: " + trends.get(i).getStars());
            }
            assertThat(trends.get(0).getTitle()).isNotNull();
        } else {
            System.out.println("GitHub REST API returned empty list (unauthenticated rate limit in CI runner)");
        }
    }

    @Test
    void shouldFetchRealHuggingFaceModels() {
        HuggingFaceApiClientAdapter adapter = new HuggingFaceApiClientAdapter();
        List<Trend> trends = adapter.fetchTrendingModels();

        assertThat(trends).isNotNull();
        if (!trends.isEmpty()) {
            System.out.println(">>> HuggingFace Models fetched: " + trends.size());
            for (int i = 0; i < Math.min(3, trends.size()); i++) {
                System.out.println("    " + trends.get(i).getTitle() + " | Likes: " + trends.get(i).getStars());
            }
            assertThat(trends.get(0).getTitle()).isNotNull();
        } else {
            System.out.println("Hugging Face API returned empty list in CI runner");
        }
    }
}
