package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
@ApiModel(description = "A postal address")
public class PostalAddressView {

    @ApiModelProperty("First address line")
    String line1;

    @ApiModelProperty("Second address line")
    String line2;

    @ApiModelProperty("Postal code")
    String postalCode;

    @ApiModelProperty("City")
    String city;

    @ApiModelProperty("Country")
    String country;
}
