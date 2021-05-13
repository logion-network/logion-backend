package logion.backend.api;

import java.util.UUID;
import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.Ss58Address;
import logion.backend.model.tokenizationrequest.TokenizationRequestDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/token-request", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class TokenRequestController {

    @PostMapping
    public TokenRequestView createTokenRequest(@RequestBody CreateTokenRequestView createTokenRequestView) {
        var tokenDescription = TokenizationRequestDescription.builder()
                .legalOfficerAddress(new Ss58Address(createTokenRequestView.getLegalOfficerAddress()))
                .requesterAddress(new Ss58Address(createTokenRequestView.getRequesterAddress()))
                .requestedTokenName(createTokenRequestView.getRequestedTokenName())
                .bars(createTokenRequestView.getBars())
                .build();
        var id = UUID.randomUUID();
        var request = tokenizationRequestFactory.newPendingTokenizationRequest(id, tokenDescription);

        request = tokenizationRequestCommands.addTokenizationRequest(request);

        return TokenRequestView.builder()
                .id(request.getId())
                .requestedTokenName(request.getTokenDescription().getRequestedTokenName())
                .legalOfficerAddress(request.getTokenDescription().getLegalOfficerAddress().getRawValue())
                .requesterAddress(request.getTokenDescription().getRequesterAddress().getRawValue())
                .bars(request.getTokenDescription().getBars())
                .status(request.getStatus())
                .build();
    }

    @Autowired
    private TokenizationRequestFactory tokenizationRequestFactory;

    @Autowired
    private TokenizationRequestCommands tokenizationRequestCommands;

    @PostMapping("{requestId}/reject")
    public void rejectTokenRequest(@PathVariable String requestId) {
        tokenizationRequestCommands.rejectTokenizationRequest(UUID.fromString(requestId));
    }
}
