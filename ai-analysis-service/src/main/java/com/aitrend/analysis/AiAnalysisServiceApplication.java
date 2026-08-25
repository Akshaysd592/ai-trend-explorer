package com.aitrend.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class AiAnalysisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAnalysisServiceApplication.class, args);
    }
}
