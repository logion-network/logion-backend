package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import logion.backend.annotation.View;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "Information about the created Protection Request")
public class ProtectionRequestView {

    @ApiModelProperty("The ID of created Protection Request")
    UUID id;

    @ApiModelProperty("The SS58 address of the requester")
    String requesterAddress;

    @ApiModelProperty("The identification data of the requester")
    UserIdentityView userIdentity;

    @ApiModelProperty("The postal address of the requester")
    PostalAddressView userPostalAddress;

    @ApiModelProperty("The Legal Officer decisions")
    List<LegalOfficerDecisionView> decisions;

    @ApiModelProperty("The creation timestamp")
    LocalDateTime createdOn;

    @ApiModelProperty("True if the the protection request is also a recovery request")
    @Getter(value=AccessLevel.NONE)
    @Setter(value=AccessLevel.NONE)
    boolean isRecovery;

    public boolean getIsRecovery() {
        return isRecovery;
    }

    @ApiModelProperty("If this request is a recovery request, tells the address to recover")
    String addressToRecover;
}
