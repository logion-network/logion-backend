package logion.backend.model.tokenizationrequest;

import java.time.LocalDateTime;
import java.util.UUID;
import logion.backend.annotation.Factory;

@Factory
public class TokenizationRequestFactory {

    public TokenizationRequestAggregateRoot newPendingTokenizationRequest(UUID id, TokenizationRequestDescription tokenDescription, LocalDateTime createdOn) {
        var request = new TokenizationRequestAggregateRoot();
        request.id = id;
        request.status = TokenizationRequestStatus.PENDING;
        request.requesterAddress = tokenDescription.getRequesterAddress();
        request.legalOfficerAddress = tokenDescription.getLegalOfficerAddress();
        request.requestedTokenName = tokenDescription.getRequestedTokenName();
        request.bars = tokenDescription.getBars();
        request.createdOn = createdOn;
        return request;
    }
}
