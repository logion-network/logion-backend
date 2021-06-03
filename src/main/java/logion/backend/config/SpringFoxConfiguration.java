package logion.backend.config;

import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static java.util.Collections.emptyList;

@Configuration
public class SpringFoxConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.any())
          .paths(Predicate.not(PathSelectors.regex("/api/error.*")))
          .build()
          .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
          "Logion off-chain service API",
          "API for data and services managed off-chain.",
          "0.1",
          "https://logion.network/",
          new Contact("Logion Team", "https://logion.network/", "info@logion.network"),
          "Apache 2.0",
          "http://www.apache.org/licenses/LICENSE-2.0",
          emptyList());
    }
}
