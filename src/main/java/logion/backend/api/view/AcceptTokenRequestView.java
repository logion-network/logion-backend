package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
public class AcceptTokenRequestView extends SignedOperationView {
}
