package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateProtectionRequestView extends SignedOperationView {

    String requesterAddress;
    UserIdentityView userIdentity;
    PostalAddressView userPostalAddress;
    String[] legalOfficerAddresses;
}
