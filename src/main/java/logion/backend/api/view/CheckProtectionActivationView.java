package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "A Protection Request to check for protection")
public class CheckProtectionActivationView extends SignedOperationView {

    @ApiModelProperty("The SS58 address of the user, used only for signature. Can be either regular user or legal officer")
    String userAddress;
}
