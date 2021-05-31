package logion.backend.model.tokenizationrequest;

import java.util.Optional;
import logion.backend.model.DefaultAddresses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/tokenization_requests.sql")
class TokenizationRequestRepositoryTest {

    @Test
    void findByLegalOfficerAddress() {
        var query = FetchRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedStatus(TokenizationRequestStatus.PENDING)
                .build();
        var requests = repository.findBy(query);
        assertThat(requests.size(), is(2));
    }

    @Autowired
    private TokenizationRequestRepository repository;

    @Test
    void findAcceptedWithoutAssetDescription() {
        var query = FetchRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedStatus(TokenizationRequestStatus.ACCEPTED)
                .expectedTokenName(Optional.of("MYT4"))
                .build();
        var requests = repository.findBy(query);
        assertThat(requests.size(), is(1));

        var request = requests.get(0);
        assertTrue(request.getAssetDescription().isEmpty());
        assertThat(request.acceptSessionTokenHash, notNullValue());
    }

    @Test
    void findAcceptedWithAssetDescription() {
        var query = FetchRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedStatus(TokenizationRequestStatus.ACCEPTED)
                .expectedTokenName(Optional.of("MYT5"))
                .build();
        var requests = repository.findBy(query);
        assertThat(requests.size(), is(1));

        var request = requests.get(0);
        assertTrue(request.getAssetDescription().isPresent());
        assertThat(request.acceptSessionTokenHash, nullValue());
    }
}
