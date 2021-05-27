package logion.backend.api;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.AcceptTokenRequestView;
import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.FetchRequestsResponseView;
import logion.backend.api.view.FetchRequestsSpecificationView;
import logion.backend.api.view.RejectTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.Ss58Address;
import logion.backend.model.Signature;
import logion.backend.model.tokenizationrequest.FetchRequestsSpecification;
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
import static java.util.stream.StreamSupport.stream;
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

        signature.verify(createTokenRequestView.getSignature())
                .withSs58Address(tokenDescription.getRequesterAddress())
                .withMessageBuiltFrom(
                        legalOfficerAddress.getRawValue(),
                        tokenDescription.getRequesterAddress().getRawValue(),
                        tokenDescription.getRequestedTokenName(),
                        tokenDescription.getBars()
                );
        var id = UUID.randomUUID();
        var request = tokenizationRequestFactory.newPendingTokenizationRequest(id, tokenDescription, LocalDateTime.now());
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
                .rejectReason(request.getRejectReason())
                .createdOn(request.getCreatedOn())
                .decisionOn(request.getDecisionOn())
                .build();
    }

    @PostMapping(value = "{requestId}/reject")
    public void rejectTokenRequest(@PathVariable String requestId, @RequestBody RejectTokenRequestView rejectTokenRequestView) {
        var legalOfficerAddress = new Ss58Address(rejectTokenRequestView.getLegalOfficerAddress());
        signature.verify(rejectTokenRequestView.getSignature())
                .withSs58Address(legalOfficerAddress)
                .withMessageBuiltFrom(
                        rejectTokenRequestView.getLegalOfficerAddress(),
                        requestId,
                        rejectTokenRequestView.getRejectReason()
                );
        tokenizationRequestCommands.rejectTokenizationRequest(UUID.fromString(requestId), rejectTokenRequestView.getRejectReason(), LocalDateTime.now());
    }

    @PostMapping(value = "{requestId}/accept")
    public void acceptTokenRequest(@PathVariable String requestId, @RequestBody AcceptTokenRequestView acceptTokenRequestView) {
        var legalOfficerAddress = new Ss58Address(acceptTokenRequestView.getLegalOfficerAddress());
        signature.verify(acceptTokenRequestView.getSignature())
                .withSs58Address(legalOfficerAddress)
                .withMessageBuiltFrom(
                        acceptTokenRequestView.getLegalOfficerAddress(),
                        requestId
                );
        tokenizationRequestCommands.acceptTokenizationRequest(UUID.fromString(requestId), LocalDateTime.now());
    }

    @Autowired
    private Signature signature;

    @PutMapping
    @RestQuery
    public FetchRequestsResponseView fetchRequests(@RequestBody FetchRequestsSpecificationView specificationView) {
        var specification = FetchRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.ofNullable(specificationView.getLegalOfficerAddress()).map(Ss58Address::new))
                .expectedRequesterAddress(Optional.ofNullable(specificationView.getRequesterAddress()).map(Ss58Address::new))
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
