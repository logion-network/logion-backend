package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The description of an asset created on-chain")
public class AssetDescriptionView {

    @ApiModelProperty("The ID (64-bits unsigned integer) of the asset")
    String assetId;

    @ApiModelProperty("The number of allowed decimals for token amounts")
    Integer decimals;
}
