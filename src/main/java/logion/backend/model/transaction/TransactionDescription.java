package logion.backend.model.transaction;

import java.time.LocalDateTime;
import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class TransactionDescription {

    Ss58Address from;
    Ss58Address to;
    long transferValue;
    long tip;
    long fee;
    long reserved;
    String pallet;
    String method;
    LocalDateTime createdOn;
}
