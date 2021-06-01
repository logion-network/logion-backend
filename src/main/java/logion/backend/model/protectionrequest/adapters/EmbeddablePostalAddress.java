package logion.backend.model.protectionrequest.adapters;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EmbeddablePostalAddress {

    String line1;
    String line2;
    String postalCode;
    String city;
    String country;

}
