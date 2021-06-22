package logion.backend.model.protectionrequest;

import java.util.Set;
import java.util.UUID;
import logion.backend.annotation.Factory;
import logion.backend.model.Ss58Address;

@Factory
public class ProtectionRequestFactory {

    public ProtectionRequestAggregateRoot newProtectionRequest(
            UUID id,
            ProtectionRequestDescription description,
            Set<Ss58Address> legalOfficerAddresses) {
        var request = new ProtectionRequestAggregateRoot();
        request.id = id;
        request.requesterAddress = description.getRequesterAddress();
        request.setUserIdentityDescription(description.getUserIdentity());
        request.setUserPostalAddress(description.getUserPostalAddress());
        request.setLegalOfficerDecisions(legalOfficerAddresses, description.getCreatedOn());
        request.createdOn = description.getCreatedOn();
        request.isRecovery = description.isRecovery();
        if(description.isRecovery()) {
            request.addressToRecover = description.getAddressToRecover().orElse(null);
        }
        request.status = ProtectionRequestStatus.PENDING;
        return request;
    }
}
