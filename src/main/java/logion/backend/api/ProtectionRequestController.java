package logion.backend.api;

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
import logion.backend.model.protectionrequest.UserIdentity;
import logion.backend.util.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/protection-request", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ProtectionRequestController {

    public static final String RESOURCE = "protection-request";

    @PostMapping
    public ProtectionRequestView createProtectionRequest(@RequestBody CreateProtectionRequestView createProtectionRequestView) {
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

    @Autowired
    private ProtectionRequestFactory protectionRequestFactory;

    @Autowired
    private ProtectionRequestCommands protectionRequestCommands;

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
                .status(legalOfficerDecision.getStatus().name())
                .build();
    }
}
