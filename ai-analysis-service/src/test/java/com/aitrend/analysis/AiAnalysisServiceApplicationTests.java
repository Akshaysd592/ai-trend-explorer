package com.aitrend.analysis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.listener.auto-startup=false",
    "spring.kafka.admin.auto-create=false"
})
class AiAnalysisServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context, Kafka config, and components initialize cleanly
    }
}
