package logion.backend.api.view;

import lombok.Value;

@Value
public class RejectTokenRequestView {

    String legalOfficerAddress;
    String signature;
}
