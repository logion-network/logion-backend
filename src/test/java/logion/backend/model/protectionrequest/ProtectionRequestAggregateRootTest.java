package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ProtectionRequestAggregateRootTest {

    @Test
    void setUserIdentityDescription() {
        var expected = UserIdentity.builder()
                .email("john.doe@logion.network")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234")
                .build();
        ProtectionRequestAggregateRoot request = new ProtectionRequestAggregateRoot();
        request.setUserIdentityDescription(expected);

        var actual = request.getDescription().getUserIdentity();
        assertThat(actual, is(expected));
    }

    @Test
    void setLegalOfficerDecisions() {
        ProtectionRequestAggregateRoot request = new ProtectionRequestAggregateRoot();
        Set<Ss58Address> legalOfficerAddresses = Set.of(DefaultAddresses.ALICE, DefaultAddresses.BOB);
        request.setLegalOfficerDecisions(legalOfficerAddresses, LocalDateTime.now());
        Set<Ss58Address> legalOfficers = request.getLegalOfficerDecisionDescriptions()
                .stream()
                .peek(description -> assertThat(description.getStatus(), is(LegalOfficerDecisionStatus.PENDING)))
                .map(LegalOfficerDecisionDescription::getLegalOfficerAddress)
                .collect(Collectors.toSet());
        assertThat(legalOfficers, hasItems(DefaultAddresses.ALICE, DefaultAddresses.BOB));
    }

    @Test
    void setUserPostalAddress() {
        var expected = PostalAddress.builder()
                .line1("Place de le République Française, 10")
                .line2("boite 15")
                .postalCode("4000")
                .city("Liège")
                .country("Belgium")
                .build();
        ProtectionRequestAggregateRoot request = new ProtectionRequestAggregateRoot();
        request.setUserPostalAddress(expected);

        var actual = request.getDescription().getUserPostalAddress();
        assertThat(actual, is(expected));
    }
}
