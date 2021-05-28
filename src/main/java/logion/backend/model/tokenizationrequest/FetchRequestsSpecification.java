package logion.backend.model.tokenizationrequest;

import java.util.Optional;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@Value
@Builder
public class FetchRequestsSpecification {

    @Default Optional<Ss58Address> expectedLegalOfficer = Optional.empty();

    TokenizationRequestStatus expectedStatus;

    @Default Optional<Ss58Address> expectedRequesterAddress = Optional.empty();

    @Default Optional<String> expectedTokenName = Optional.empty();
}
