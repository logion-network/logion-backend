package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The acceptance data")
public class TokenRequestAcceptedView {

    @ApiModelProperty("The session token to provide when setting the asset description")
    String sessionToken;
}
