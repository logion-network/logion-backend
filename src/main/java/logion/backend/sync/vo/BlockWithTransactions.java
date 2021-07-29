package logion.backend.sync.vo;

import java.time.LocalDateTime;
import java.util.List;
import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class BlockWithTransactions {

    long blockNumber;
    LocalDateTime timestamp;
    List<Transaction> transactions;
}
