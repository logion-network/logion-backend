package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The new (recovery) and old (to recover) account data")
public class RecoveryInfoView {

    ProtectionRequestView recoveryAccount;
    ProtectionRequestView accountToRecover;
}
