package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
@ApiModel(description = "The asset description and session token")
public class SetAssetDescriptionView {

    @ApiModelProperty("The asset description")
    AssetDescriptionView description;

    @ApiModelProperty("The session token")
    String sessionToken;
}
