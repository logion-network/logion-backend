package logion.backend.model.tokenizationrequest;

import java.util.UUID;
import logion.backend.annotation.Factory;

@Factory
public class TokenizationRequestFactory {

    public TokenizationRequestAggregateRoot newPendingTokenizationRequest(UUID id, TokenizationRequestDescription tokenDescription) {
        var request = new TokenizationRequestAggregateRoot();
        request.id = id;
        request.status = TokenizationRequestStatus.PENDING;
        request.requesterAddress = tokenDescription.getRequesterAddress();
        request.legalOfficerAddress = tokenDescription.getLegalOfficerAddress();
        request.requestedTokenName = tokenDescription.getRequestedTokenName();
        request.bars = tokenDescription.getBars();
        request.createdOn = tokenDescription.getCreatedOn();
        return request;
    }
}
