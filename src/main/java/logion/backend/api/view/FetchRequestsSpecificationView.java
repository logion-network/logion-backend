package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import logion.backend.annotation.View;
import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The specification for fetching Tokenization Requests")
public class FetchRequestsSpecificationView {

    @ApiModelProperty("The SS58 address of the legal officer in expected Tokenization Requests")
    String legalOfficerAddress;

    @ApiModelProperty("The status in expected Tokenization Requests")
    TokenizationRequestStatus status;

    @ApiModelProperty("The SS58 address of the requester in expected Tokenization Requests")
    String requesterAddress;
}
