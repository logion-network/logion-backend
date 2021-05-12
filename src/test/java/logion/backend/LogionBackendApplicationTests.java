package logion.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LogionBackendApplicationTests {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void contextLoads() {
        assertEquals("/api", contextPath);
    }

}
