package logion.backend.sync.vo;

import java.math.BigInteger;
import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@ValueObject
@Value
@Builder
public class Transaction {

    int extrinsicIndex;
    String from;
    String to;
    @Default
    BigInteger transferValue = BigInteger.ZERO;
    @Default
    BigInteger tip = BigInteger.ZERO;
    @Default
    BigInteger fee = BigInteger.ZERO;
    @Default
    BigInteger reserved = BigInteger.ZERO;
    String pallet;
    String method;
}
