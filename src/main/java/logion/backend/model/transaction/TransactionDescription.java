package logion.backend.model.transaction;

import java.math.BigInteger;
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
    BigInteger transferValue;
    BigInteger tip;
    BigInteger fee;
    BigInteger reserved;
    String pallet;
    String method;
    LocalDateTime createdOn;
}
