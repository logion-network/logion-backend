package logion.backend.sync.vo;

import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class Transaction {

    int extrinsicIndex;
    String from;
    String to;
    long transferValue;
    long tip;
    long fee;
    long reserved;
    String pallet;
    String method;
}
