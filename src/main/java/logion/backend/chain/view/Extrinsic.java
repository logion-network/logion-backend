package logion.backend.chain.view;

import com.fasterxml.jackson.databind.JsonNode;
import logion.backend.annotation.View;
import lombok.Data;

@View
@Data
public class Extrinsic {

    Method method;
    Signature signature;
    JsonNode args;
    Long tip;
    Info info;
    Event[] events;
    boolean success;
    boolean paysFee;
}
