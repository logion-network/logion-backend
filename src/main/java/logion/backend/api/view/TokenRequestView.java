package logion.backend.api.view;

import java.util.UUID;
import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenRequestView {

    UUID id;
    String requestedTokenName;
    String legalOfficerAddress;
    String requesterAddress;
    int bars;
    TokenizationRequestStatus status;
}
