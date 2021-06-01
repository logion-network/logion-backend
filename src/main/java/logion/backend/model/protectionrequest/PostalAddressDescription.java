package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@ValueObject
public class PostalAddressDescription {

    String line1;
    String line2;
    String postalCode;
    String city;
    String country;
}
