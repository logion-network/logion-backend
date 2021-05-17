package logion.backend.api;

import java.util.UUID;
import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.QueryTokenRequestResponseView;
import logion.backend.api.view.QueryTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.Ss58Address;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestFactory;
import logion.backend.model.tokenizationrequest.TokenizationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;
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
        return toView(request);
    }

    @Autowired
    private TokenizationRequestFactory tokenizationRequestFactory;

    @Autowired
    private TokenizationRequestCommands tokenizationRequestCommands;

    private TokenRequestView toView(TokenizationRequestAggregateRoot request) {
        var tokenDescription = request.getDescription();
        return TokenRequestView.builder()
                .id(request.getId())
                .requestedTokenName(tokenDescription.getRequestedTokenName())
                .legalOfficerAddress(tokenDescription.getLegalOfficerAddress().getRawValue())
                .requesterAddress(tokenDescription.getRequesterAddress().getRawValue())
                .bars(tokenDescription.getBars())
                .status(request.getStatus())
                .build();
    }

    @PostMapping("{requestId}/reject")
    public void rejectTokenRequest(@PathVariable String requestId) {
        tokenizationRequestCommands.rejectTokenizationRequest(UUID.fromString(requestId));
    }

    @PutMapping
    public QueryTokenRequestResponseView queryTokenRequests(@RequestBody QueryTokenRequestView queryTokenRequestView) {
        var legalOfficerAddress = new Ss58Address(queryTokenRequestView.getLegalOfficerAddress());
        var requests = tokenizationRequestRepository.findByLegalOfficerAddress(legalOfficerAddress);
        return QueryTokenRequestResponseView.builder()
                .requests(requests.stream()
                        .filter(request -> request.getDescription().getLegalOfficerAddress().equals(legalOfficerAddress))
                        .filter(request -> request.getStatus() == queryTokenRequestView.getStatus())
                        .map(this::toView)
                        .collect(toList()))
                .build();
    }

    @Autowired
    private TokenizationRequestRepository tokenizationRequestRepository;
}
