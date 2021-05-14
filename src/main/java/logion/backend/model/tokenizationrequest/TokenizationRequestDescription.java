package logion.backend.model.tokenizationrequest;

import logion.backend.annotation.ValueObject;
import logion.backend.model.Ss58Address;
import lombok.Builder;
import lombok.Value;

/**
 * The tokenization request description submitted by a requester.
 */
@Value
@Builder
@ValueObject
public class TokenizationRequestDescription {

    String requestedTokenName;

    Ss58Address legalOfficerAddress;

    Ss58Address requesterAddress;

    int bars;
}
