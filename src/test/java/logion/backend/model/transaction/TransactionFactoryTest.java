package logion.backend.model.transaction;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

class TransactionFactoryTest {

    @Test
    void newTransaction() {
        // Given
        var blockNumber = 123456L;
        var extrinsicIndex = 5;
        var description = givenDescription();
        // When
        Transaction transaction = new TransactionFactory().newTransaction(blockNumber, extrinsicIndex, description);
        // Then
        assertThat(transaction.getDescription(), is(description));
        assertThat(transaction.id.blockNumber, is(blockNumber));
        assertThat(transaction.id.extrinsicIndex, is(extrinsicIndex));
    }

    private TransactionDescription givenDescription() {
        return TransactionDescription.builder()
                .from(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"))
                .to(Optional.of(new Ss58Address("5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY")))
                .fee(BigInteger.valueOf(12L))
                .transferValue(BigInteger.valueOf(34L))
                .tip(BigInteger.valueOf(56L))
                .reserved(BigInteger.valueOf(78L))
                .pallet("recovery")
                .method("createRecovery")
                .createdOn(LocalDateTime.now())
                .build();
    }
}
