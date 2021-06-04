package logion.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDateTime;
import java.util.UUID;
import logion.backend.api.view.CreateProtectionRequestView;
import logion.backend.api.view.LegalOfficerDecisionView;
import logion.backend.api.view.PostalAddressView;
import logion.backend.api.view.ProtectionRequestView;
import logion.backend.api.view.UserIdentityView;
import logion.backend.commands.ProtectionRequestCommands;
import logion.backend.model.Signature;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.LegalOfficerDecisionDescription;
import logion.backend.model.protectionrequest.PostalAddress;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestDescription;
import logion.backend.model.protectionrequest.ProtectionRequestFactory;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import logion.backend.model.protectionrequest.UserIdentity;
import logion.backend.util.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.ALL_VALUE;
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

    @GetMapping(consumes = ALL_VALUE)
    @ApiOperation(
            value = "Fetch an existing Protection Request",
            notes = "No authentication required yet"
    )
    public ProtectionRequestView fetchProtectionRequest(
            @ApiParam(value = "The SS58 address of the protection requester", name = "requesterAddress", required = true)
            @RequestParam("requesterAddress") String requesterAddress) {
        return protectionRequestRepository.findByRequesterAddress(new Ss58Address(requesterAddress))
                .map(this::toView)
                .orElseThrow(ProtectionRequestRepository.requestNotFound);
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
                .decisions(CollectionMapper.mapSet(this::toView, request.getLegalOfficerDecisionDescriptions()))
                .build();
    }

    private LegalOfficerDecisionView toView(LegalOfficerDecisionDescription legalOfficerDecision) {
        return LegalOfficerDecisionView.builder()
                .legalOfficerAddress(legalOfficerDecision.getLegalOfficerAddress().getRawValue())
                .status(legalOfficerDecision.getStatus())
                .build();
    }
}
