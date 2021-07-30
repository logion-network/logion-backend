package logion.backend.model.protectionrequest;

import java.util.Optional;
import java.util.Set;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

import static java.util.Collections.emptySet;

@Value
@Builder
public class FetchProtectionRequestsSpecification {

    @NonNull
    @Default
    Optional<Ss58Address> expectedRequesterAddress = Optional.empty();

    @NonNull
    @Default
    Optional<Ss58Address> expectedLegalOfficer = Optional.empty();

    @NonNull
    @Default
    Set<LegalOfficerDecisionStatus> expectedDecisionStatuses = emptySet();

    @NonNull
    @Default
    Optional<ProtectionRequestStatus> expectedProtectionRequestStatus = Optional.empty();

    @NonNull
    @Default
    ProtectionRequestKind kind = ProtectionRequestKind.ANY;
}
