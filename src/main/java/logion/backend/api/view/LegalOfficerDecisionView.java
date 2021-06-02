package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
public class LegalOfficerDecisionView {

    String legalOfficerAddress;
    String status;

}
