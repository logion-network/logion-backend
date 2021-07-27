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
@ApiModel(description = "A transaction between 2 accounts")
public class TransactionView {

    @ApiModelProperty("The SS58 address of the account from which the transaction is done")
    String from;

    @ApiModelProperty("The SS58address of the account to which the transaction is done. May be null.")
    String to;

    @ApiModelProperty("The pallet that created the transaction.")
    String pallet;

    @ApiModelProperty("The method that created the transaction.")
    String method;

    @ApiModelProperty("The value of the transfer, iff the transaction is a transfer.")
    String transferValue;

    @ApiModelProperty("The tip of the transaction.")
    String tip;

    @ApiModelProperty("The fee of the transaction.")
    String fee;

    @ApiModelProperty("The reserved amount of the transaction.")
    String reserved;

    @ApiModelProperty("The total amount of the transaction.")
    String total;

    @ApiModelProperty("The timestamp of the transaction")
    LocalDateTime createdOn;
}
