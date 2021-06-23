package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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

    @ApiModelProperty("True if the the protection request is also a recovery request")
    @Getter(value=AccessLevel.NONE)
    @Setter(value=AccessLevel.NONE)
    boolean isRecovery;

    public boolean getIsRecovery() {
        return isRecovery;
    }

    public void setIsRecovery(boolean value) {
        isRecovery = value;
    }

    @ApiModelProperty("If this request is a recovery request, tells the address to recover")
    String addressToRecover;
}
