package logion.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SidecarConfiguration {

    @Value("${logion.sidecar.uri:http://127.0.0.1:8081}")
    private String sideCarRootUri;

    @Bean
    public RestTemplate sidecar(RestTemplateBuilder builder) {
        return builder.rootUri(sideCarRootUri)
                .build();
    }
}
