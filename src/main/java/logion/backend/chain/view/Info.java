package logion.backend.chain.view;

import java.math.BigInteger;
import logion.backend.annotation.View;
import lombok.Data;

@View
@Data
public class Info {

    BigInteger partialFee;
    String error;
}
