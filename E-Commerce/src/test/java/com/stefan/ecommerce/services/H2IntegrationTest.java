package com.stefan.ecommerce.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class H2IntegrationTest {
    @Autowired
    private Environment env;

    @Test
    void testH2DatabaseIsUsed() {
        String url = env.getProperty("spring.datasource.url");
        assertNotNull(url);
        assertTrue(url.contains("h2:mem"), "H2 in-memory database should be used for tests, but was: " + url);
    }
} 