package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class ProtectionRequestFactoryTest {

    @Test
    void createsPendingRequests() {
        givenRequestId(UUID.randomUUID());
        ProtectionRequestDescription description = ProtectionRequestDescription.builder()
                .requesterAddress(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"))
                .build();
        givenProtectionRequestDescription(description);
        var userIdentityDescription = UserIdentityDescription.builder()
                .email("john.doe@logion.network")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234")
                .build();
        givenUserIdentityDescription(userIdentityDescription);

        var postalAddressDescription = PostalAddressDescription.builder()
                .line1("Place de le République Française, 10")
                .line2("boite 15")
                .postalCode("4000")
                .city("Liège")
                .country("Belgium")
                .build();
        givenPostalAddressDescription(postalAddressDescription);

        givenLegalOfficers(DefaultAddresses.ALICE, DefaultAddresses.BOB);

        givenCreatedOn(LocalDateTime.now());
        whenCreatingProtectionRequest();

        thenProtectionRequestCreated();
    }

    private void givenRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    private UUID requestId;

    private void givenProtectionRequestDescription(ProtectionRequestDescription description) {
        protectionRequestDescription = description;
    }

    private void givenUserIdentityDescription(UserIdentityDescription userIdentityDescription) {
        this.userIdentityDescription = userIdentityDescription;
    }

    private UserIdentityDescription userIdentityDescription;

    private void givenPostalAddressDescription(PostalAddressDescription postalAddressDescription) {
        this.postalAddressDescription = postalAddressDescription;
    }

    private PostalAddressDescription postalAddressDescription;

    private void givenLegalOfficers(Ss58Address... legalOfficers) {
        this.legalOfficerDecisionDescriptions = Stream.of(legalOfficers)
                .map(LegalOfficerDecisionDescription.builder()::legalOfficerAddress)
                .map(LegalOfficerDecisionDescription.LegalOfficerDecisionDescriptionBuilder::build)
                .collect(Collectors.toSet());
    }

    private Set<LegalOfficerDecisionDescription> legalOfficerDecisionDescriptions;

    private void givenCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    private LocalDateTime createdOn;

    private ProtectionRequestDescription protectionRequestDescription;

    private void whenCreatingProtectionRequest() {
        protectionRequest = new ProtectionRequestFactory().newProtectionRequest(
                requestId,
                protectionRequestDescription,
                userIdentityDescription,
                postalAddressDescription,
                legalOfficerDecisionDescriptions,
                createdOn
        );
    }

    private ProtectionRequestAggregateRoot protectionRequest;

    private void thenProtectionRequestCreated() {
        assertThat(protectionRequest.getId(), equalTo(requestId));
        assertThat(protectionRequest.getDescription(), equalTo(protectionRequestDescription));
        assertThat(protectionRequest.getUserIdentityDescription(), equalTo(userIdentityDescription));
        assertThat(protectionRequest.getUserPostalAddressDescription(), equalTo(postalAddressDescription));
        assertThat(protectionRequest.getLegalOfficerDecisionDescriptions(), equalTo(legalOfficerDecisionDescriptions));
        assertThat(protectionRequest.getCreatedOn(), equalTo(createdOn));
    }
}
