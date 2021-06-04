package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import logion.backend.annotation.View;
import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "A description of the created Tokenization Request")
public class TokenRequestView {

    @ApiModelProperty("The ID of created tokenization request")
    UUID id;

    @ApiModelProperty("The name of the token to create")
    String requestedTokenName;

    @ApiModelProperty("The SS58 address of the legal officer that will issue the tokens upon acceptance")
    String legalOfficerAddress;

    @ApiModelProperty("The SS58 address of the tokenization requester")
    String requesterAddress;

    @ApiModelProperty("The number of gold bars serving as the underlying of the tokens to create")
    int bars;

    @ApiModelProperty("The request's status")
    TokenizationRequestStatus status;

    @ApiModelProperty("If status is 'REJECTED', the reason of the rejection")
    String rejectReason;

    @ApiModelProperty("The creation timestamp")
    LocalDateTime createdOn;

    @ApiModelProperty("The decision timestamp (if status is 'ACCEPTED' or 'REJECTED')")
    LocalDateTime decisionOn;

    @ApiModelProperty("The created asset description (once created on-chain)")
    AssetDescriptionView assetDescription;
}
