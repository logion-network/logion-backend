package logion.backend.model.transfer;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import logion.backend.annotation.AggregateRoot;
import logion.backend.model.Ss58Address;
import logion.backend.model.adapters.Ss58AddressConverter;

@AggregateRoot
@Entity(name = "transfer")
public class Transfer {

    @EmbeddedId
    TransferId id;

    @Convert(converter = Ss58AddressConverter.class)
    Ss58Address from;

    @Convert(converter = Ss58AddressConverter.class)
    Ss58Address to;

    long value;

    LocalDateTime createdOn;

    @Embeddable
    static class TransferId implements Serializable {
        long blockId;
        int extrinsicIndex;
    }
}
