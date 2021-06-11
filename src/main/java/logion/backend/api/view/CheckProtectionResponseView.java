package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The response of the check protection")
public class CheckProtectionResponseView {

    @ApiModelProperty("<code>true</code> if and only if the user has submitted a protection request to the legal " +
            "officer, and the legal officer accepted. <code>false</code> in all other cases.")
    boolean protection;
}
