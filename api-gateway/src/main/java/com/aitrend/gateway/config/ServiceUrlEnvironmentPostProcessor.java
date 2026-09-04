package com.aitrend.gateway.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class ServiceUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> map = new HashMap<>();
        sanitize(environment, map, "AUTH_SERVICE_URL", "http://localhost:8082");
        sanitize(environment, map, "TREND_SERVICE_URL", "http://localhost:8081");
        sanitize(environment, map, "AI_ANALYSIS_SERVICE_URL", "http://localhost:8083");

        if (!map.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("serviceUrlSanitizer", map));
        }
    }

    private void sanitize(ConfigurableEnvironment env, Map<String, Object> map, String key, String defaultUrl) {
        String val = env.getProperty(key);
        if (val == null || val.isBlank()) {
            val = defaultUrl;
        } else if (!val.startsWith("http://") && !val.startsWith("https://")) {
            val = "http://" + val;
        }
        map.put(key, val);
    }
}
