package logion.backend.api.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateTokenRequestView extends SignedOperationView {

    String requestedTokenName;
    String legalOfficerAddress;
    String requesterAddress;
    int bars;
}
