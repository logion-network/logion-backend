package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "A transfer between 2 accounts")
public class TransferView {

    @ApiModelProperty("The SS58 address of the account from which the transfer is done")
    String from;

    @ApiModelProperty("The SS58address of the account to which the transfer is done")
    String to;

    @ApiModelProperty("The value of the transfer")
    String value;

    @ApiModelProperty("The timestamp of the transfer")
    LocalDateTime createdOn;
}
