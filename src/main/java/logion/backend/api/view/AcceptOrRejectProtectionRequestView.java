package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "The Protection Request to accept")
public class AcceptOrRejectProtectionRequestView extends SignedOperationView {

    @ApiModelProperty("The SS58 address of the legal officer accepting or rejecting the protection request")
    String legalOfficerAddress;
}
