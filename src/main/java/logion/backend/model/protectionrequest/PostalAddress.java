package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class PostalAddress {

    String line1;
    String line2;
    String postalCode;
    String city;
    String country;
}
