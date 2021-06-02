package logion.backend.model.protectionrequest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import logion.backend.annotation.DddEntity;
import logion.backend.model.Ss58Address;
import logion.backend.model.adapters.Ss58AddressConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DddEntity
@Entity(name = "legal_officer_decision")
public class LegalOfficerDecision {

    public LegalOfficerDecisionDescription getDescription() {
        return LegalOfficerDecisionDescription.builder()
                .legalOfficerAddress(id.legalOfficerAddress)
                .status(status)
                .createdOn(createdOn)
                .build();
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class LegalOfficerDecisionId implements Serializable {
        UUID requestId;
        @Convert(converter = Ss58AddressConverter.class)
        Ss58Address legalOfficerAddress;
    }

    @Getter
    @EmbeddedId
    LegalOfficerDecisionId id;

    @Getter
    @Enumerated(EnumType.STRING)
    LegalOfficerDecisionStatus status;

    LocalDateTime createdOn;

    @Getter
    LocalDateTime decisionOn;
}
