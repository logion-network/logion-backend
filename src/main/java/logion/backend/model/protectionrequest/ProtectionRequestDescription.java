package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Optional;
import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder(builderClassName = "Builder", buildMethodName = "build")
public class ProtectionRequestDescription {

    Ss58Address requesterAddress;
    UserIdentity userIdentity;
    PostalAddress userPostalAddress;
    LocalDateTime createdOn;
    boolean isRecovery;
    Optional<Ss58Address> addressToRecover;

    public static class Builder {

        public ProtectionRequestDescription build() {
            if(addressToRecover == null) {
                addressToRecover = Optional.empty();
            }
            if (isRecovery && addressToRecover.isEmpty()) {
                throw new IllegalArgumentException("Address to recover must be provided");
            }
            return new ProtectionRequestDescription(requesterAddress,
                    userIdentity,
                    userPostalAddress,
                    createdOn,
                    isRecovery,
                    addressToRecover);
        }
    }
}
