package logion.backend.model.protectionrequest;

import java.util.Optional;
import java.util.Set;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

import static java.util.Collections.emptySet;

@Value
@Builder
public class FetchProtectionRequestsSpecification {

    @Builder.Default
    Optional<Ss58Address> expectedRequesterAddress = Optional.empty();

    @Builder.Default
    Optional<Ss58Address> expectedLegalOfficer = Optional.empty();

    @Builder.Default
    Set<LegalOfficerDecisionStatus> expectedStatuses = emptySet();

    @Builder.Default
    ProtectionRequestKind kind = ProtectionRequestKind.ANY;
}
