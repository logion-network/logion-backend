package logion.backend.api.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcceptTokenRequestView extends SignedOperationView {

    String legalOfficerAddress;

}
