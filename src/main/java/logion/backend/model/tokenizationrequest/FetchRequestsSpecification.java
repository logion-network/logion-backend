package logion.backend.model.tokenizationrequest;

import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FetchRequestsSpecification {

    Ss58Address expectedLegalOfficer;

    TokenizationRequestStatus expectedStatus;
}
