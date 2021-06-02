package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
public class UserIdentityView {

    String firstName;
    String lastName;
    String email;
    String phoneNumber;
}
