package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ProtectionRequestAggregateRootTest {

    @Test
    void setUserIdentityDescription() {
        var expected = UserIdentityDescription.builder()
                .email("john.doe@logion.network")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234")
                .build();
        ProtectionRequestAggregateRoot request = new ProtectionRequestAggregateRoot();
        request.setUserIdentityDescription(expected);

        var actual = request.getUserIdentityDescription();
        assertThat(actual, is(expected));
    }

    @Test
    void setLegalOfficerDecisions() {
        ProtectionRequestAggregateRoot request = new ProtectionRequestAggregateRoot();
        Set<LegalOfficerDecisionDescription> legalOfficerDecisionDescriptions =
                Stream.of(DefaultAddresses.ALICE, DefaultAddresses.BOB)
                        .map(LegalOfficerDecisionDescription.builder()::legalOfficerAddress)
                        .map(LegalOfficerDecisionDescription.LegalOfficerDecisionDescriptionBuilder::build)
                        .collect(Collectors.toSet());
        request.setLegalOfficerDecisions(legalOfficerDecisionDescriptions, LocalDateTime.now());
        Set<Ss58Address> legalOfficers = request.getLegalOfficerDecisionDescriptions()
                .stream()
                .map(LegalOfficerDecisionDescription::getLegalOfficerAddress)
                .collect(Collectors.toSet());
        assertThat(legalOfficers, hasItems(DefaultAddresses.ALICE, DefaultAddresses.BOB));
    }

    @Test
    void setUserPostalAddress() {
        var expected = PostalAddressDescription.builder()
                .line1("Place de le République Française, 10")
                .line2("boite 15")
                .postalCode("4000")
                .city("Liège")
                .country("Belgium")
                .build();
        ProtectionRequestAggregateRoot request = new ProtectionRequestAggregateRoot();
        request.setUserPostalAddress(expected);

        var actual = request.getUserPostalAddressDescription();
        assertThat(actual, is(expected));
    }
}
