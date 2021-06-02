package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
public class PostalAddressView {

    String line1;
    String line2;
    String postalCode;
    String city;
    String country;
}
