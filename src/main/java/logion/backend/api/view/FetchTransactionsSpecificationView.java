package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import lombok.Data;

@View
@Data
@ApiModel(description = "The specification for fetching Tokenization Requests")
public class FetchTransactionsSpecificationView {

    @ApiModelProperty("The SS58 address of the account from or to which the expected transaction is done")
    String address;
}
