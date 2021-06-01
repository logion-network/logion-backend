package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@ValueObject
public class UserIdentityDescription {

    String firstName;
    String lastName;
    String email;
    String phoneNumber;
}
