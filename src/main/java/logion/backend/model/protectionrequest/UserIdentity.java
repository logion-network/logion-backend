package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@ValueObject
@Value
@Builder
public class UserIdentity {

    String firstName;
    String lastName;
    String email;
    String phoneNumber;
}
