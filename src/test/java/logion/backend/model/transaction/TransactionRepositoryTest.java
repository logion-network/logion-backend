package logion.backend.model.transaction;

import java.util.stream.Stream;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/transactions.sql")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @ParameterizedTest
    @MethodSource
    void findByAddress(String address, int expectedNumberOfResults) {
        var transactions = transactionRepository.findByAddress(new Ss58Address(address));
        assertThat(transactions.size(), is(expectedNumberOfResults));
    }

    private static Stream<Arguments> findByAddress() {
        return Stream.of(
                Arguments.of("5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY", 2),
                Arguments.of("5CSbpCKSTvZefZYddesUQ9w6NDye2PHbf12MwBZGBgzGeGoo", 1),
                Arguments.of("Unknown", 0)
        );
    }
}
