package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@ValueObject
public class LegalOfficerDecisionDescription {

    Ss58Address legalOfficerAddress;
    @Builder.Default
    LegalOfficerDecisionStatus status = LegalOfficerDecisionStatus.PENDING;

}
