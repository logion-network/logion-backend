package logion.backend.model.tokenizationrequest;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import logion.backend.annotation.AggregateRoot;
import logion.backend.model.Ss58Address;
import logion.backend.model.adapters.Ss58AddressConverter;
import lombok.Getter;

@AggregateRoot
@Entity(name = "tokenization_request")
public class TokenizationRequestAggregateRoot {

    public void reject(String reason) {
        if(status != TokenizationRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject non-pending request");
        }

        status = TokenizationRequestStatus.REJECTED;
        rejectReason = reason;
        decisionOn = LocalDateTime.now();
    }

    public TokenizationRequestDescription getDescription() {
        return TokenizationRequestDescription.builder()
                .legalOfficerAddress(legalOfficerAddress)
                .requesterAddress(requesterAddress)
                .requestedTokenName(requestedTokenName)
                .bars(bars)
                .build();
    }

    @Id
    @Getter
    UUID id;

    String requestedTokenName;

    @Convert(converter = Ss58AddressConverter.class)
    Ss58Address legalOfficerAddress;

    @Convert(converter = Ss58AddressConverter.class)
    Ss58Address requesterAddress;

    int bars;

    @Getter
    @Enumerated(EnumType.STRING)
    TokenizationRequestStatus status;

    @Getter
    String rejectReason;

    @Getter
    LocalDateTime createdOn;

    @Getter
    LocalDateTime decisionOn;

    TokenizationRequestAggregateRoot() {

    }
}
