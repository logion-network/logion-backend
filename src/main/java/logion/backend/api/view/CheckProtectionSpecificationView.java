package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The specification to check that a user is protected by legal officer")
public class CheckProtectionSpecificationView {

    @ApiModelProperty("The SS58 address of the legal officer")
    String legalOfficerAddress;

    @ApiModelProperty("The SS58 address of the user")
    String userAddress;
}
