package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
@ApiModel(description = "Physical person identification data")
public class UserIdentityView {

    @ApiModelProperty("First name")
    String firstName;

    @ApiModelProperty("Last name")
    String lastName;

    @ApiModelProperty("E-mail")
    String email;

    @ApiModelProperty("Phone number")
    String phoneNumber;
}
