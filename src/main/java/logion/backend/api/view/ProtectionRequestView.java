package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.UUID;
import logion.backend.annotation.View;
import lombok.Builder;
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
}
