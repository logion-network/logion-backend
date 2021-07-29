package logion.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ApiConfiguration {

    @Bean
    @Primary
    public ObjectMapper apiObjectMapper(Jackson2ObjectMapperBuilder mapperBuilder) {
        return mapperBuilder.build();
    }
}
