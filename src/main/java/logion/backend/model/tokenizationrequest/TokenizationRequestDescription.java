package logion.backend.model.tokenizationrequest;

import java.time.LocalDateTime;
import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

/**
 * The tokenization request description submitted by a requester.
 */
@ValueObject
@Value
@Builder
public class TokenizationRequestDescription {

    String requestedTokenName;

    Ss58Address legalOfficerAddress;

    Ss58Address requesterAddress;

    int bars;

    LocalDateTime createdOn;
}
