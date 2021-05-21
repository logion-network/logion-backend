package logion.backend.api.view;

import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FetchRequestsSpecificationView {

    String legalOfficerAddress;
    TokenizationRequestStatus status;
    String requesterAddress;
}
