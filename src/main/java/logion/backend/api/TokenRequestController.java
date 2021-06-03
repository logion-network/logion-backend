package logion.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.AcceptTokenRequestView;
import logion.backend.api.view.AssetDescriptionView;
import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.FetchRequestsResponseView;
import logion.backend.api.view.FetchRequestsSpecificationView;
import logion.backend.api.view.RejectTokenRequestView;
import logion.backend.api.view.SetAssetDescriptionView;
import logion.backend.api.view.TokenRequestAcceptedView;
import logion.backend.api.view.TokenRequestView;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.Signature;
import logion.backend.model.Ss58Address;
import logion.backend.model.tokenizationrequest.AssetDescription;
import logion.backend.model.tokenizationrequest.AssetId;
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
@Api(tags = "Tokenization Requests", value = "TokenRequestController", description = "Handling of Tokenization Requests")
public class TokenRequestController {

    public static final String RESOURCE = "token-request";

    @PostMapping
    @ApiOperation(
        value = "Creates a new Tokenization Request",
        notes = "<p>The signature's resource is <code>token-request</code>, the operation <code>create</code> and the additional fields are:</p><ul><li><code>legalOfficerAddress</code></li><li><code>requestedTokenName</code></li><li><code>bars</code></li></ul><p>"
    )
    public TokenRequestView createTokenRequest(
            @RequestBody
            @ApiParam(value = "Tokenization Request creation data", name = "body", required = true)
            CreateTokenRequestView createTokenRequestView) {
        var tokenDescription = TokenizationRequestDescription.builder()
                .legalOfficerAddress(new Ss58Address(createTokenRequestView.getLegalOfficerAddress()))
                .requesterAddress(new Ss58Address(createTokenRequestView.getRequesterAddress()))
                .requestedTokenName(createTokenRequestView.getRequestedTokenName())
                .bars(createTokenRequestView.getBars())
                .createdOn(LocalDateTime.now())
                .build();

        var legalOfficerAddress = tokenDescription.getLegalOfficerAddress();

        signature.verify(createTokenRequestView.getSignature())
                .withSs58Address(tokenDescription.getRequesterAddress())
                .withResource(RESOURCE)
                .withOperation("create")
                .withTimestamp(createTokenRequestView.getSignedOn())
                .withMessageBuiltFrom(
                        legalOfficerAddress.getRawValue(),
                        tokenDescription.getRequestedTokenName(),
                        tokenDescription.getBars()
                );
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
                .rejectReason(request.getRejectReason())
                .createdOn(tokenDescription.getCreatedOn())
                .decisionOn(request.getDecisionOn())
                .assetDescription(request.getAssetDescription().map(this::toView).orElse(null))
                .build();
    }

    private AssetDescriptionView toView(AssetDescription description) {
        return AssetDescriptionView.builder()
                .assetId(description.getAssetId().getValue())
                .decimals(description.getDecimals())
                .build();
    }

    @PostMapping(value = "{requestId}/reject")
    @ApiOperation(
        value = "Rejects a Tokenization Request",
        notes = "<p>The signature's resource is <code>token-request</code>, the operation <code>reject</code> and the additional fields are:</p><ul><li><code>requestId</code></li><li><code>rejectReason</code></li></ul><p>"
    )
    public void rejectTokenRequest(
            @PathVariable
            @ApiParam(value = "The ID of the request to reject")
            String requestId,
            @RequestBody
            @ApiParam(value = "Tokenization Request rejection data", name = "body", required = true)
            RejectTokenRequestView rejectTokenRequestView) {
        var id = UUID.fromString(requestId);
        var request = tokenizationRequestRepository.findById(id)
                .orElseThrow(TokenizationRequestRepository.requestNotFound);
        signature.verify(rejectTokenRequestView.getSignature())
                .withSs58Address(request.getDescription().getLegalOfficerAddress())
                .withResource(RESOURCE)
                .withOperation("reject")
                .withTimestamp(rejectTokenRequestView.getSignedOn())
                .withMessageBuiltFrom(
                        requestId,
                        rejectTokenRequestView.getRejectReason()
                );
        tokenizationRequestCommands.rejectTokenizationRequest(id, rejectTokenRequestView.getRejectReason(), LocalDateTime.now());
    }

    @PostMapping(value = "{requestId}/accept")
    @ApiOperation(
        value = "Accepts a Tokenization Request",
        notes = "<p>The signature's resource is <code>token-request</code>, the operation <code>accept</code> and the additional field is the <code>requestId</code>.<p>"
    )
    public TokenRequestAcceptedView acceptTokenRequest(
            @PathVariable
            @ApiParam(value = "The ID of the request to accept")
            String requestId,
            @RequestBody
            @ApiParam(value = "Tokenization Request acceptance data", name = "body")
            AcceptTokenRequestView acceptTokenRequestView) {
        var id = UUID.fromString(requestId);
        var request = tokenizationRequestRepository.findById(id)
                .orElseThrow(TokenizationRequestRepository.requestNotFound);
        signature.verify(acceptTokenRequestView.getSignature())
                .withSs58Address(request.getDescription().getLegalOfficerAddress())
                .withResource(RESOURCE)
                .withOperation("accept")
                .withTimestamp(acceptTokenRequestView.getSignedOn())
                .withMessageBuiltFrom(requestId);
        String sessionToken = tokenizationRequestCommands.acceptTokenizationRequest(UUID.fromString(requestId), LocalDateTime.now());
        return TokenRequestAcceptedView.builder()
                .sessionToken(sessionToken)
                .build();
    }

    @Autowired
    private Signature signature;

    @PostMapping(value = "{requestId}/asset")
    @ApiOperation(
        value = "Sets the asset description of an accepted Tokenization Request",
        notes = "The session token to provide in the body is received upon Tokenization Request acceptance"
    )
    public void setAssetDescription(
            @PathVariable
            @ApiParam(value = "The ID of the request")
            String requestId,
            @RequestBody
            @ApiParam(value = "The description of the asset created for the Tokenization Request", name = "body")
            SetAssetDescriptionView requestBody) {
        var sessionToken = requestBody.getSessionToken();
        var description = AssetDescription.builder()
                .assetId(new AssetId(requestBody.getDescription().getAssetId()))
                .decimals(requestBody.getDescription().getDecimals())
                .build();
        tokenizationRequestCommands.setAssetDescription(UUID.fromString(requestId), sessionToken, description);
    }

    @PutMapping
    @RestQuery
    @ApiOperation(
        value = "Lists Tokenization Requests based on a given specification",
        notes = "No authentication required yet"
    )
    public FetchRequestsResponseView fetchRequests(
            @RequestBody
            @ApiParam(value = "The specification for fetching Tokenization Requests", name = "body")
            FetchRequestsSpecificationView specificationView) {
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
