package logion.backend.chain.view;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigInteger;
import logion.backend.annotation.View;
import lombok.Data;

@View
@Data
public class Extrinsic {

    Method method;
    Signature signature;
    JsonNode args;
    BigInteger tip;
    Info info;
    Event[] events;
    boolean success;
    boolean paysFee;
}
