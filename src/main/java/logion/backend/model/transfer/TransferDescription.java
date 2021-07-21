package logion.backend.model.transfer;

import java.time.LocalDateTime;
import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class TransferDescription {

    Ss58Address from;
    Ss58Address to;
    long value;
    LocalDateTime createdOn;
}
