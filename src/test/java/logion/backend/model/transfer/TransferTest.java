package logion.backend.model.transfer;

import java.time.LocalDateTime;
import logion.backend.model.Ss58Address;
import logion.backend.model.transfer.Transfer.TransferId;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TransferTest {

    @Test
    void getDescription() {
        var transfer = transfer();
        var description = transfer.getDescription();
        assertThat(description.getFrom(), is(transfer.from));
        assertThat(description.getTo(), is(transfer.to));
        assertThat(description.getCreatedOn(), is(transfer.createdOn));
        assertThat(description.getValue(), is(transfer.value));
    }

    private Transfer transfer() {
        var transfer = new Transfer();
        transfer.id = new TransferId();
        transfer.id.blockId = 1;
        transfer.id.extrinsicIndex = 1;
        transfer.from = new Ss58Address("from");
        transfer.to = new Ss58Address("to");
        transfer.createdOn = LocalDateTime.now();
        transfer.value = 123456;
        return transfer;
    }
}
