package logion.backend.model.protectionrequest;

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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/protection_requests.sql")
class ProtectionRequestRepositoryTest {

    @Test
    void findByRequesterAddress() {
        Ss58Address requesterAddress = new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
        var request = repository.findByRequesterAddress(requesterAddress);
        assertThat(request.isPresent(), is(true));

        var protectionRequest = request.get();

        Set<LegalOfficerDecisionDescription> legalOfficerDecisionDescriptions = protectionRequest.getLegalOfficerDecisionDescriptions();

        assertThat(legalOfficerDecisionDescriptions.size(), is(2));

        var legalOfficerAddresses = legalOfficerDecisionDescriptions.stream()
                .map(LegalOfficerDecisionDescription::getLegalOfficerAddress)
                .collect(Collectors.toSet());

        assertThat(legalOfficerAddresses, hasItems(DefaultAddresses.ALICE, DefaultAddresses.BOB));
    }

    @Autowired
    private ProtectionRequestRepository repository;
}
