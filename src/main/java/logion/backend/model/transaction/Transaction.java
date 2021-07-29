package logion.backend.model.transaction;

import java.io.Serializable;
import java.math.BigInteger;
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
@Entity(name = "transaction")
@Table(indexes = {
        @Index(name = "idx_transaction_from_address", columnList = "from_address"),
        @Index(name = "idx_transaction_to_address", columnList = "to_address")
})
public class Transaction {

    private static final int AMOUNT_PRECISION = 50;

    public TransactionDescription getDescription() {
        return TransactionDescription.builder()
                .from(from)
                .to(to)
                .transferValue(transferValue)
                .tip(tip)
                .fee(fee)
                .reserved(reserved)
                .pallet(pallet)
                .method(method)
                .createdOn(createdOn)
                .build();
    }
    
    void setDescription(TransactionDescription description) {
        this.from = description.getFrom();
        this.to = description.getTo();
        this.transferValue = description.getTransferValue();
        this.tip = description.getTip();
        this.fee = description.getFee();
        this.reserved = description.getReserved();
        this.pallet = description.getPallet();
        this.method = description.getMethod();
        this.createdOn = description.getCreatedOn();
    }

    @EmbeddedId
    TransactionId id;

    @Convert(converter = Ss58AddressConverter.class)
    @Column(name = "from_address", nullable = false)
    Ss58Address from;

    @Convert(converter = Ss58AddressConverter.class)
    @Column(name = "to_address")
    Ss58Address to;

    @Column(nullable = false, precision = AMOUNT_PRECISION)
    BigInteger transferValue;

    @Column(nullable = false, precision = AMOUNT_PRECISION)
    BigInteger tip;

    @Column(nullable = false, precision = AMOUNT_PRECISION)
    BigInteger fee;

    @Column(nullable = false, precision = AMOUNT_PRECISION)
    BigInteger reserved;

    @Column(nullable = false)
    String pallet;

    @Column(nullable = false)
    String method;

    @Column(nullable = false)
    LocalDateTime createdOn;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class TransactionId implements Serializable {
        long blockNumber;
        int extrinsicIndex;
    }
}
