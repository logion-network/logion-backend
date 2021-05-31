package logion.backend.api.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RejectTokenRequestView extends SignedOperationView {

    String rejectReason;
}
