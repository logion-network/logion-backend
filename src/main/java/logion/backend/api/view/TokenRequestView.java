package logion.backend.api.view;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class TokenRequestView {

    UUID id;

}
