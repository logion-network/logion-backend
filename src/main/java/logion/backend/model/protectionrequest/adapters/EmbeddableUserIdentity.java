package logion.backend.model.protectionrequest.adapters;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EmbeddableUserIdentity {

    String firstName;
    String lastName;
    String email;
    String phoneNumber;

}
