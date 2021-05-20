package logion.backend.api;

import java.util.UUID;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.FetchRequestsResponseView;
import logion.backend.api.view.FetchRequestsSpecificationView;
import logion.backend.api.view.RejectTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.Ss58Address;
import logion.backend.model.Subkey;
import logion.backend.model.tokenizationrequest.FetchRequestsSpecification;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestFactory;
import logion.backend.model.tokenizationrequest.TokenizationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.http.MediaType.ALL_VALUE;
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

        var legalOfficerAddress = tokenDescription.getLegalOfficerAddress();
        var message =  new StringBuilder()
                .append(legalOfficerAddress.getRawValue())
                .append('-')
                .append(tokenDescription.getRequesterAddress().getRawValue())
                .append('-')
                .append(tokenDescription.getRequestedTokenName())
                .append('-')
                .append(tokenDescription.getBars())
                .toString();
        var signatureValid = subkey.verify(createTokenRequestView.getSignature())
                .withSs58Address(tokenDescription.getRequesterAddress())
                .withMessage(message);
        if(!signatureValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify signature");
        }

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

    @PostMapping(value = "{requestId}/reject", consumes = ALL_VALUE)
    public void rejectTokenRequest(@PathVariable String requestId, @RequestBody RejectTokenRequestView rejectTokenRequestView) {
        var legalOfficerAddress = new Ss58Address(rejectTokenRequestView.getLegalOfficerAddress());
        var message = rejectTokenRequestView.getLegalOfficerAddress() + "-" + requestId;
        var signatureValid = subkey.verify(rejectTokenRequestView.getSignature())
                .withSs58Address(legalOfficerAddress)
                .withMessage(message);
        if(!signatureValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify signature");
        }
        tokenizationRequestCommands.rejectTokenizationRequest(UUID.fromString(requestId));
    }

    @Autowired
    private Subkey subkey;

    @PutMapping
    @RestQuery
    public FetchRequestsResponseView fetchRequests(@RequestBody FetchRequestsSpecificationView specificationView) {
        var legalOfficerAddress = new Ss58Address(specificationView.getLegalOfficerAddress());
        var specification = FetchRequestsSpecification.builder()
                .expectedLegalOfficer(legalOfficerAddress)
                .expectedStatus(specificationView.getStatus())
                .build();
        var requests = tokenizationRequestRepository.findBy(specification);
        return FetchRequestsResponseView.builder()
                .requests(stream(requests.spliterator(), false)
                        .map(this::toView)
                        .collect(toList()))
                .build();
    }

    @Autowired
    private TokenizationRequestRepository tokenizationRequestRepository;
}
