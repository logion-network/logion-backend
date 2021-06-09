package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "A Protection Request to create")
public class CreateProtectionRequestView extends SignedOperationView {

    @ApiModelProperty("The SS58 address of the requester")
    String requesterAddress;

    @ApiModelProperty("The identification data of the requester")
    UserIdentityView userIdentity;

    @ApiModelProperty("The postal address of the requester")
    PostalAddressView userPostalAddress;

    @ApiModelProperty("The list of legal officers (SS58 addresses) to whom protection is requested")
    String[] legalOfficerAddresses;
}
