package com.aitrend.auth.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class JdbcUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = environment.getProperty("spring.datasource.url");
        if (url == null) {
            url = environment.getProperty("SPRING_DATASOURCE_URL");
        }
        if (url != null && (url.startsWith("postgresql://") || url.startsWith("postgres://"))) {
            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", "jdbc:" + url);
            map.put("SPRING_DATASOURCE_URL", "jdbc:" + url);
            environment.getPropertySources().addFirst(new MapPropertySource("jdbcUrlSanitizer", map));
        }
    }
}
