package logion.backend.model.transfer;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import logion.backend.annotation.AggregateRoot;
import logion.backend.model.Ss58Address;
import logion.backend.model.adapters.Ss58AddressConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AggregateRoot
@Entity(name = "transfer")
@Table(indexes = {
        @Index(name = "idx_transfer_from_address", columnList = "from_address"),
        @Index(name = "idx_transfer_to_address", columnList = "to_address")
})
public class Transfer {

    public TransferDescription getDescription() {
        return TransferDescription.builder()
                .from(from)
                .to(to)
                .value(value)
                .createdOn(createdOn)
                .build();
    }

    @EmbeddedId
    TransferId id;

    @Convert(converter = Ss58AddressConverter.class)
    @Column(name = "from_address", nullable = false)
    Ss58Address from;

    @Convert(converter = Ss58AddressConverter.class)
    @Column(name = "to_address", nullable = false)
    Ss58Address to;

    long value;

    @Column(nullable = false)
    LocalDateTime createdOn;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TransferId implements Serializable {
        long blockId;
        int extrinsicIndex;
    }
}
