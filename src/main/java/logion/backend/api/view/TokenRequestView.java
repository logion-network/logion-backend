package logion.backend.api.view;

import java.time.LocalDateTime;
import java.util.UUID;
import logion.backend.annotation.View;
import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
public class TokenRequestView {

    UUID id;
    String requestedTokenName;
    String legalOfficerAddress;
    String requesterAddress;
    int bars;
    TokenizationRequestStatus status;
    String rejectReason;
    LocalDateTime createdOn;
    LocalDateTime decisionOn;
    AssetDescriptionView assetDescription;
}
