package logion.backend.api.view;

import java.util.Set;
import java.util.UUID;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
public class ProtectionRequestView {

    UUID id;
    Set<LegalOfficerDecisionView> decisions;

}
