package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class ProtectionRequestDescription {

    Ss58Address requesterAddress;
    UserIdentity userIdentity;
    PostalAddress userPostalAddress;
    LocalDateTime createdOn;
}
