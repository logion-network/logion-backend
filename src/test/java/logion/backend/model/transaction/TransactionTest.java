package logion.backend.model.transaction;

import java.math.BigInteger;
import java.time.LocalDateTime;
import logion.backend.model.Ss58Address;
import logion.backend.model.transaction.Transaction.TransactionId;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TransactionTest {

    @Test
    void getDescription() {
        var transaction = transaction();
        var description = transaction.getDescription();
        assertThat(description.getFrom(), is(transaction.from));
        assertThat(description.getTo(), is(transaction.to));
        assertThat(description.getCreatedOn(), is(transaction.createdOn));
        assertThat(description.getTransferValue(), is(transaction.transferValue));
    }

    private Transaction transaction() {
        var transaction = new Transaction();
        transaction.id = new TransactionId();
        transaction.id.blockNumber = 1;
        transaction.id.extrinsicIndex = 1;
        transaction.from = new Ss58Address("from");
        transaction.to = new Ss58Address("to");
        transaction.createdOn = LocalDateTime.now();
        transaction.transferValue = BigInteger.valueOf(123456L);
        return transaction;
    }
}
