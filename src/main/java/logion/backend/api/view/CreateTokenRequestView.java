package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "A Tokenization Request to create")
public class CreateTokenRequestView extends SignedOperationView {

    @ApiModelProperty("The name of the token to create")
    String requestedTokenName;

    @ApiModelProperty("The SS58 address of the legal officer that will issue the tokens upon acceptance")
    String legalOfficerAddress;

    @ApiModelProperty("The SS58 address of the tokenization requester")
    String requesterAddress;

    @ApiModelProperty("The number of gold bars serving as the underlying of the tokens to create")
    int bars;
}
