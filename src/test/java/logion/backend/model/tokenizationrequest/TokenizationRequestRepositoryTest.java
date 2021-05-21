package logion.backend.model.tokenizationrequest;

import logion.backend.model.DefaultAddresses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/tokenization_requests.sql")
class TokenizationRequestRepositoryTest {

    @Test
    void findByLegalOfficerAddress() {
        var query = FetchRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedRequesterAddress(Optional.empty())
                .expectedStatus(TokenizationRequestStatus.PENDING)
                .build();
        var requests = repository.findBy(query);
        assertThat(requests.size(), is(2));
    }

    @Autowired
    private TokenizationRequestRepository repository;
}
