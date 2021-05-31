package logion.backend.api.view;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SignedOperationView {

    String signature;
    LocalDateTime signedOn;
}
