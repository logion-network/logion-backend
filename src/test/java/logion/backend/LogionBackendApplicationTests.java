package logion.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@SpringBootTest
@AutoConfigureTestDatabase
class LogionBackendApplicationTests {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private ObjectMapper apiObjectMapper;

    @Autowired
    @Qualifier("sidecarObjectMapper")
    private ObjectMapper sidecarObjectMapper;

    @Test
    void contextLoads() {
        assertEquals("/api", contextPath);
    }

    @Test
    void objectMappers() {
        assertNotSame(apiObjectMapper, sidecarObjectMapper);
    }

}
