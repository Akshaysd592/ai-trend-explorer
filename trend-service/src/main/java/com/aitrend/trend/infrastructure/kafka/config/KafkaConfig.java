package com.aitrend.trend.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka topic configuration and setup for trend-service.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.trend-ingested:ai.trends.ingested}")
    private String trendIngestedTopic;

    /**
     * Configures KafkaAdmin with fatalIfBrokerNotAvailable=false so that
     * application/tests start smoothly even if the Kafka cluster is temporarily offline.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setFatalIfBrokerNotAvailable(false);
        admin.setAutoCreate(true);
        return admin;
    }

    /**
     * Auto-creates the topic with 3 partitions and replication factor 1
     * if it does not already exist in the Kafka cluster.
     */
    @Bean
    public NewTopic trendIngestedTopic() {
        return TopicBuilder.name(trendIngestedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
