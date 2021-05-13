package logion.backend.model.tokenizationrequest;

import logion.backend.model.DefaultAddresses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/tokenization_requests.sql")
class TokenizationRequestRepositoryTest {

    @Test
    void findByLegalOfficerAddress() {
        var requests = repository.findByLegalOfficerAddress(DefaultAddresses.ALICE);
        assertThat(requests.size(), is(2));
    }

    @Autowired
    private TokenizationRequestRepository repository;
}
