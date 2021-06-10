package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import logion.backend.annotation.View;
import logion.backend.model.protectionrequest.LegalOfficerDecisionStatus;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "Legal Officer decision")
public class LegalOfficerDecisionView {

    @ApiModelProperty("The SS58 address of the legal officer")
    String legalOfficerAddress;

    @ApiModelProperty("The decision status")
    LegalOfficerDecisionStatus status;

    @ApiModelProperty("If status is 'REJECTED', the reason of the rejection")
    String rejectReason;

    @ApiModelProperty("The creation timestamp")
    LocalDateTime createdOn;

    @ApiModelProperty("The decision timestamp (if status is 'ACCEPTED' or 'REJECTED')")
    LocalDateTime decisionOn;

}
