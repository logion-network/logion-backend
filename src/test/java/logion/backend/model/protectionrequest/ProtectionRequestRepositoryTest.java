package logion.backend.model.protectionrequest;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/protection_requests.sql")
class ProtectionRequestRepositoryTest {

    @Test
    void findBy() {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedStatuses(Set.of(
                        LegalOfficerDecisionStatus.ACCEPTED,
                        LegalOfficerDecisionStatus.REJECTED))
                .build();
        var results = repository.findBy(specification);
        assertThat(results.size(), is(2));
        results.stream()
                .flatMap(root -> root.getLegalOfficerDecisionDescriptions().stream())
                .filter(legalOfficerDecisionDescription -> legalOfficerDecisionDescription.getLegalOfficerAddress().equals(DefaultAddresses.ALICE))
                .map(LegalOfficerDecisionDescription::getStatus)
                .forEach(status -> assertThat(status, anyOf(
                        is(LegalOfficerDecisionStatus.ACCEPTED),
                        is(LegalOfficerDecisionStatus.REJECTED))));
    }

    @Test
    void findByRequesterAddress() {
        var requesterAddress = new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedRequesterAddress(Optional.of(requesterAddress))
                .build();
        var request = repository.findBy(specification);
        assertThat(request.size(), is(1));

        var protectionRequest = request.get(0);

        var legalOfficerDecisionDescriptions = protectionRequest.getLegalOfficerDecisionDescriptions();

        assertThat(legalOfficerDecisionDescriptions.size(), is(2));

        var legalOfficerAddresses = legalOfficerDecisionDescriptions.stream()
                .map(LegalOfficerDecisionDescription::getLegalOfficerAddress)
                .collect(Collectors.toSet());

        assertThat(legalOfficerAddresses, hasItems(DefaultAddresses.ALICE, DefaultAddresses.BOB));
    }

    @Autowired
    private ProtectionRequestRepository repository;
}
