package com.aitrend.trend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=true",
    "spring.cache.type=none",
    "spring.kafka.listener.auto-startup=false",
    "spring.kafka.admin.auto-create=false",
    "jwt.secret=aiTrendExplorerSuperSecretKeyThatIsAtLeast256BitsLongForHMACSHA256"
})
class TrendServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context, Liquibase migration, Kafka and JPA entities load cleanly
    }
}
