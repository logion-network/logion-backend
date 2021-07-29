package logion.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SidecarConfiguration {

    @Value("${logion.sidecar.uri:http://127.0.0.1:8081}")
    private String sideCarRootUri;

    @Bean
    public RestTemplate sidecar(RestTemplateBuilder builder, ObjectMapper sideCarObjectMapper) {
        return builder.rootUri(sideCarRootUri)
                .messageConverters(new MappingJackson2HttpMessageConverter(sideCarObjectMapper))
                .build();
    }

    @Bean("sidecarObjectMapper")
    public ObjectMapper sidecarObjectMapper(Jackson2ObjectMapperBuilder mapperBuilder) {
        return mapperBuilder.build();
    }
}
