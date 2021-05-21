package logion.backend.model.tokenizationrequest;

import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class FetchRequestsSpecification {

    Optional<Ss58Address> expectedLegalOfficer;

    TokenizationRequestStatus expectedStatus;

    Optional<Ss58Address> expectedRequesterAddress;
}
