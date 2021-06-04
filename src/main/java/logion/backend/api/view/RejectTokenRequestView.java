package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "The Tokenization Request to reject")
public class RejectTokenRequestView extends SignedOperationView {

    @ApiModelProperty("The rejection reason")
    String rejectReason;
}
