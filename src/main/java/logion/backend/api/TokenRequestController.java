package logion.backend.api;

import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/token-request", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class TokenRequestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping
    public TokenRequestView createTokenRequest(@RequestBody CreateTokenRequestView createTokenRequestView) {
        UUID id = UUID.randomUUID();
        logger.info("Successfully created {} with id={}", createTokenRequestView, id);
        return TokenRequestView.builder()
                .id(id)
                .build();
    }
}
