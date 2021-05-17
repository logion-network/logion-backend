package logion.backend.api.view;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateTokenRequestView {

    String requestedTokenName;
    String legalOfficerAddress;
    String requesterAddress;
    int bars;

}
