package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateTokenRequestView extends SignedOperationView {

    String requestedTokenName;
    String legalOfficerAddress;
    String requesterAddress;
    int bars;
}
