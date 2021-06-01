package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import logion.backend.annotation.Factory;
import logion.backend.model.Ss58Address;

@Factory
public class ProtectionRequestFactory {

    public ProtectionRequestAggregateRoot newProtectionRequest(
            UUID id,
            ProtectionRequestDescription description,
            Set<Ss58Address> legalOfficerAddresses,
            LocalDateTime createdOn) {
        var request = new ProtectionRequestAggregateRoot();
        request.id = id;
        request.requesterAddress = description.getRequesterAddress();
        request.setUserIdentityDescription(description.getUserIdentity());
        request.setUserPostalAddress(description.getUserPostalAddress());
        request.setLegalOfficerDecisions(legalOfficerAddresses, createdOn);
        request.createdOn = createdOn;
        return request;
    }
}
