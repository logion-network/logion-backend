package logion.backend.api.view;

import lombok.Value;

@Value
public class AcceptTokenRequestView {

    String legalOfficerAddress;
    String signature;

}
