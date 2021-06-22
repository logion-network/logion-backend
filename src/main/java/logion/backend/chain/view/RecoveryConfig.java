package logion.backend.chain.view;

import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
public class RecoveryConfig {

    long delayPeriod;
    long deposit;
    String[] friends;
    int threshold;
}
