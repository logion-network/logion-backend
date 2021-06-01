package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ProtectionRequestFactoryTest {

    @Test
    void createsPendingRequests() {
        givenRequestId(UUID.randomUUID());
        var userIdentity = UserIdentity.builder()
                .email("john.doe@logion.network")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234")
                .build();
        givenUserIdentity(userIdentity);
        var postalAddress = PostalAddress.builder()
                .line1("Place de le République Française, 10")
                .line2("boite 15")
                .postalCode("4000")
                .city("Liège")
                .country("Belgium")
                .build();
        givenPostalAddress(postalAddress);
        var description = ProtectionRequestDescription.builder()
                .requesterAddress(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"))
                .userIdentity(userIdentity)
                .userPostalAddress(postalAddress)
                .build();
        givenProtectionRequestDescription(description);
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

    private void givenUserIdentity(UserIdentity userIdentity) {
        this.userIdentity = userIdentity;
    }

    private UserIdentity userIdentity;

    private void givenPostalAddress(PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

    private PostalAddress postalAddress;

    private void givenLegalOfficers(Ss58Address... legalOfficers) {
        this.legalOfficerAddresses = Set.of(legalOfficers);
    }

    private Set<Ss58Address> legalOfficerAddresses;

    private void givenCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    private LocalDateTime createdOn;

    private ProtectionRequestDescription protectionRequestDescription;

    private void whenCreatingProtectionRequest() {
        protectionRequest = new ProtectionRequestFactory().newProtectionRequest(
                requestId,
                protectionRequestDescription,
                legalOfficerAddresses,
                createdOn
        );
    }

    private ProtectionRequestAggregateRoot protectionRequest;

    private void thenProtectionRequestCreated() {
        assertThat(protectionRequest.getId(), equalTo(requestId));
        assertThat(protectionRequest.getDescription(), equalTo(protectionRequestDescription));
        assertThat(protectionRequest.getDescription().getUserIdentity(), equalTo(userIdentity));
        assertThat(protectionRequest.getDescription().getUserPostalAddress(), equalTo(postalAddress));
        Set<Ss58Address> actualLegalOfficerAddresses = protectionRequest.getLegalOfficerDecisionDescriptions()
                .stream()
                .peek(description -> assertThat(description.getStatus(), is(LegalOfficerDecisionStatus.PENDING)))
                .map(LegalOfficerDecisionDescription::getLegalOfficerAddress)
                .collect(Collectors.toSet());
        assertThat(actualLegalOfficerAddresses, equalTo(legalOfficerAddresses));
        assertThat(protectionRequest.getCreatedOn(), equalTo(createdOn));
    }
}
