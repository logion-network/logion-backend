package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import logion.backend.annotation.Factory;

@Factory
public class ProtectionRequestFactory {

    public ProtectionRequestAggregateRoot newProtectionRequest(
            UUID id,
            ProtectionRequestDescription description,
            UserIdentityDescription userIdentityDescription,
            PostalAddressDescription userPostalAddressDescription,
            Set<LegalOfficerDecisionDescription> legalOfficers,
            LocalDateTime createdOn) {
        var request = new ProtectionRequestAggregateRoot();
        request.id = id;
        request.requesterAddress = description.getRequesterAddress();
        request.setUserIdentityDescription(userIdentityDescription);
        request.setUserPostalAddress(userPostalAddressDescription);
        request.setLegalOfficerDecisions(legalOfficers, createdOn);
        request.createdOn = createdOn;
        return request;
    }
}
