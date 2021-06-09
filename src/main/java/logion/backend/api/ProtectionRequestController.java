package logion.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.AcceptProtectionRequestView;
import logion.backend.api.view.CreateProtectionRequestView;
import logion.backend.api.view.FetchProtectionRequestsResponseView;
import logion.backend.api.view.FetchProtectionRequestsSpecificationView;
import logion.backend.api.view.LegalOfficerDecisionView;
import logion.backend.api.view.PostalAddressView;
import logion.backend.api.view.ProtectionRequestView;
import logion.backend.api.view.RejectProtectionRequestView;
import logion.backend.api.view.UserIdentityView;
import logion.backend.commands.ProtectionRequestCommands;
import logion.backend.model.Signature;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.FetchProtectionRequestsSpecification;
import logion.backend.model.protectionrequest.LegalOfficerDecisionDescription;
import logion.backend.model.protectionrequest.PostalAddress;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestDescription;
import logion.backend.model.protectionrequest.ProtectionRequestFactory;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import logion.backend.model.protectionrequest.UserIdentity;
import logion.backend.util.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/protection-request", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Api(tags = "Protection Requests", value = "ProtectionRequestController", description = "Handling of Protection Requests")
public class ProtectionRequestController {

    public static final String RESOURCE = "protection-request";

    @PostMapping
    @ApiOperation(
        value = "Creates a new Protection Request",
        notes = "<p>The signature's resource is <code>protection-request</code>, the operation <code>create</code> and the additional fields are:</p><ul><li><code>userIdentity.firstName</code></li><li><code>userIdentity.lastName</code></li><li><code>userIdentity.email</code></li><li><code>userIdentity.phoneNumber</code></li><li><code>userPostalAddress.line1</code></li><li><code>userPostalAddress.line2</code></li><li><code>userPostalAddress.postalCode</code></li><li><code>userPostalAddress.city</code></li><li><code>userPostalAddress.country</code></li><li><code>userPostalAddress.line1</code></li><li><code>legalOfficerAddresses*</code></li></ul><p>where <code>legalOfficerAddresses*</code> is the concatenation of all SS58 addresses from field <code>legalOfficerAddresses</code></p>"
    )
    public ProtectionRequestView createProtectionRequest(
            @RequestBody
            @ApiParam(value = "Protection Request creation data", name = "body", required = true)
            CreateProtectionRequestView createProtectionRequestView) {
        var userIdentity = createProtectionRequestView.getUserIdentity();
        var userPostalAddress = createProtectionRequestView.getUserPostalAddress();
        var requesterAddress = new Ss58Address(createProtectionRequestView.getRequesterAddress());
        signature.verify(createProtectionRequestView.getSignature())
                .withSs58Address(requesterAddress)
                .withResource(RESOURCE)
                .withOperation("create")
                .withTimestamp(createProtectionRequestView.getSignedOn())
                .withMessageBuiltFrom(
                        userIdentity.getFirstName(),
                        userIdentity.getLastName(),
                        userIdentity.getEmail(),
                        userIdentity.getPhoneNumber(),
                        userPostalAddress.getLine1(),
                        userPostalAddress.getLine2(),
                        userPostalAddress.getPostalCode(),
                        userPostalAddress.getCity(),
                        userPostalAddress.getCountry(),
                        createProtectionRequestView.getLegalOfficerAddresses()
                );
        var protectionRequestDescription = ProtectionRequestDescription.builder()
                .requesterAddress(requesterAddress)
                .userIdentity(fromView(userIdentity))
                .userPostalAddress(fromView(userPostalAddress))
                .createdOn(LocalDateTime.now())
                .build();
        var request = protectionRequestFactory.newProtectionRequest(
                UUID.randomUUID(),
                protectionRequestDescription,
                CollectionMapper.mapArrayToSet(Ss58Address::new, createProtectionRequestView.getLegalOfficerAddresses()));
        request = protectionRequestCommands.addProtectionRequest(request);
        return toView(request);
    }

    @PutMapping
    @RestQuery
    @ApiOperation(
            value = "Lists Protection Requests based on a given specification",
            notes = "No authentication required yet"
    )
    public FetchProtectionRequestsResponseView fetchProtectionRequests(
            @RequestBody
            @ApiParam(value = "The specification for fetching Protection Requests", name = "body")
                    FetchProtectionRequestsSpecificationView specificationView) {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.ofNullable(specificationView.getLegalOfficerAddress()).map(Ss58Address::new))
                .expectedRequesterAddress(Optional.ofNullable(specificationView.getRequesterAddress()).map(Ss58Address::new))
                .expectedStatuses(specificationView.getStatuses())
                .build();
        var requests = protectionRequestRepository.findBy(specification);
        return FetchProtectionRequestsResponseView.builder()
                .requests(CollectionMapper.mapList(this::toView, requests))
                .build();
    }

    @PostMapping(value = "{requestId}/accept")
    @ApiOperation(
            value = "Accepts a Protection Request",
            notes = "<p>The signature's resource is <code>protection-request</code>, the operation <code>accept</code> and the additional field is the <code>requestId</code>.<p>"
    )
    public void acceptTokenRequest(
            @PathVariable
            @ApiParam(value = "The ID of the request to accept")
                    String requestId,
            @RequestBody
            @ApiParam(value = "Protection Request acceptance data", name = "body")
                    AcceptProtectionRequestView acceptProtectionRequestView) {
        var id = UUID.fromString(requestId);
        var legalOfficerAddress = new Ss58Address(acceptProtectionRequestView.getLegalOfficerAddress());
        signature.verify(acceptProtectionRequestView.getSignature())
                .withSs58Address(legalOfficerAddress)
                .withResource(RESOURCE)
                .withOperation("accept")
                .withTimestamp(acceptProtectionRequestView.getSignedOn())
                .withMessageBuiltFrom(requestId);
        protectionRequestCommands.acceptProtectionRequest(id, legalOfficerAddress, LocalDateTime.now());
    }

    @PostMapping(value = "{requestId}/reject")
    @ApiOperation(
            value = "Rejects a Protection Request",
            notes = "<p>The signature's resource is <code>protection-request</code>, the operation <code>reject</code> and the additional field is the <code>requestId</code>.<p>"
    )
    public void rejectTokenRequest(
            @PathVariable
            @ApiParam(value = "The ID of the request to reject")
                    String requestId,
            @RequestBody
            @ApiParam(value = "Protection Request rejection data", name = "body")
                    RejectProtectionRequestView rejectProtectionRequestView) {
        var id = UUID.fromString(requestId);
        var legalOfficerAddress = new Ss58Address(rejectProtectionRequestView.getLegalOfficerAddress());
        String rejectReason = rejectProtectionRequestView.getRejectReason();
        signature.verify(rejectProtectionRequestView.getSignature())
                .withSs58Address(legalOfficerAddress)
                .withResource(RESOURCE)
                .withOperation("reject")
                .withTimestamp(rejectProtectionRequestView.getSignedOn())
                .withMessageBuiltFrom(
                        requestId,
                        rejectReason);
        protectionRequestCommands.rejectProtectionRequest(id, legalOfficerAddress, rejectReason, LocalDateTime.now());
    }

    @Autowired
    private ProtectionRequestFactory protectionRequestFactory;

    @Autowired
    private ProtectionRequestCommands protectionRequestCommands;

    @Autowired
    private ProtectionRequestRepository protectionRequestRepository;

    @Autowired
    private Signature signature;

    private UserIdentity fromView(UserIdentityView userIdentityView) {
        return UserIdentity.builder()
                .firstName(userIdentityView.getFirstName())
                .lastName(userIdentityView.getLastName())
                .email(userIdentityView.getEmail())
                .phoneNumber(userIdentityView.getPhoneNumber())
                .build();
    }

    private PostalAddress fromView(PostalAddressView postalAddressView) {
        return PostalAddress.builder()
                .line1(postalAddressView.getLine1())
                .line2(postalAddressView.getLine2())
                .postalCode(postalAddressView.getPostalCode())
                .city(postalAddressView.getCity())
                .country(postalAddressView.getCountry())
                .build();
    }

    private ProtectionRequestView toView(ProtectionRequestAggregateRoot request) {
        return ProtectionRequestView.builder()
                .id(request.getId())
                .userIdentity(toView(request.getDescription().getUserIdentity()))
                .userPostalAddress(toView(request.getDescription().getUserPostalAddress()))
                .decisions(CollectionMapper.mapSet(this::toView, request.getLegalOfficerDecisionDescriptions()))
                .build();
    }

    private UserIdentityView toView(UserIdentity userIdentity) {
        return UserIdentityView.builder()
                .firstName(userIdentity.getFirstName())
                .lastName(userIdentity.getLastName())
                .email(userIdentity.getEmail())
                .phoneNumber(userIdentity.getPhoneNumber())
                .build();
    }

    private PostalAddressView toView(PostalAddress postalAddress) {
        return PostalAddressView.builder()
                .line1(postalAddress.getLine1())
                .line2(postalAddress.getLine2())
                .postalCode(postalAddress.getPostalCode())
                .city(postalAddress.getCity())
                .country(postalAddress.getCountry())
                .build();
    }

    private LegalOfficerDecisionView toView(LegalOfficerDecisionDescription legalOfficerDecision) {
        return LegalOfficerDecisionView.builder()
                .legalOfficerAddress(legalOfficerDecision.getLegalOfficerAddress().getRawValue())
                .status(legalOfficerDecision.getStatus())
                .rejectReason(legalOfficerDecision.getRejectReason())
                .build();
    }
}
