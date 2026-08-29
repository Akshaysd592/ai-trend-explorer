package com.aitrend.trend.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class CloudDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(CloudDataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String rawUrl = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();

        // Check for Render/Heroku/Fly.io standard DATABASE_URL environment variable
        String envDbUrl = System.getenv("DATABASE_URL");
        if (envDbUrl == null || envDbUrl.isBlank()) {
            envDbUrl = rawUrl;
        }

        if (envDbUrl != null && (envDbUrl.startsWith("postgresql://") || envDbUrl.startsWith("postgres://"))) {
            try {
                URI uri = new URI(envDbUrl);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                String dbName = (path != null && path.length() > 1) ? path.substring(1) : "ai_trend_db";

                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + "/" + dbName;
                log.info("CloudDataSourceConfig transformed cloud URI into JDBC URL: jdbc:postgresql://{}:{}/{}", uri.getHost(), port, dbName);

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                return new HikariDataSource(config);
            } catch (Exception e) {
                log.warn("CloudDataSourceConfig failed parsing database URI, falling back to standard properties: {}", e.getMessage());
            }
        }

        return properties.initializeDataSourceBuilder().build();
    }
}
