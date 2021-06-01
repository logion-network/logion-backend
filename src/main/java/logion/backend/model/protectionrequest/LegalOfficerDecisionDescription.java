package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@ValueObject
@Value
@Builder
public class LegalOfficerDecisionDescription {

    Ss58Address legalOfficerAddress;
    @Default
    LegalOfficerDecisionStatus status = LegalOfficerDecisionStatus.PENDING;

}
