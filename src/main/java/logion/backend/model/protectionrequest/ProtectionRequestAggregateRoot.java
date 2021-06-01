package logion.backend.model.protectionrequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import logion.backend.annotation.AggregateRoot;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.adapters.EmbeddablePostalAddress;
import logion.backend.model.protectionrequest.adapters.EmbeddableUserIdentity;
import logion.backend.model.adapters.Ss58AddressConverter;
import lombok.Getter;

@AggregateRoot
@Entity(name = "protection_request")
public class ProtectionRequestAggregateRoot {

    @Id
    @Getter
    UUID id;

    @Embedded
    EmbeddableUserIdentity userIdentity;

    @Embedded
    EmbeddablePostalAddress userPostalAddress;

    @Getter
    LocalDateTime createdOn;

    @Convert(converter = Ss58AddressConverter.class)
    Ss58Address requesterAddress;

    @OneToMany(mappedBy = "id.requestId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<LegalOfficerDecision> decisions;

    public ProtectionRequestDescription getDescription() {
        return ProtectionRequestDescription.builder()
                .requesterAddress(requesterAddress)
                .build();
    }

    public void setUserIdentityDescription(UserIdentityDescription description) {
        userIdentity = new EmbeddableUserIdentity();
        userIdentity.setFirstName(description.getFirstName());
        userIdentity.setLastName(description.getLastName());
        userIdentity.setEmail(description.getEmail());
        userIdentity.setPhoneNumber(description.getPhoneNumber());
    }

    public UserIdentityDescription getUserIdentityDescription() {
        if (userIdentity == null) {
            return UserIdentityDescription.builder().build();
        }
        return UserIdentityDescription.builder()
                .firstName(userIdentity.getFirstName())
                .lastName(userIdentity.getLastName())
                .email(userIdentity.getEmail())
                .phoneNumber(userIdentity.getPhoneNumber())
                .build();
    }

    public void setUserPostalAddress(PostalAddressDescription description) {
        userPostalAddress = new EmbeddablePostalAddress();
        userPostalAddress.setLine1(description.getLine1());
        userPostalAddress.setLine2(description.getLine2());
        userPostalAddress.setPostalCode(description.getPostalCode());
        userPostalAddress.setCity(description.getCity());
        userPostalAddress.setCountry(description.getCountry());
    }

    public PostalAddressDescription getUserPostalAddressDescription() {
        if (userPostalAddress == null) {
            return PostalAddressDescription.builder().build();
        }
        return PostalAddressDescription.builder()
                .line1(userPostalAddress.getLine1())
                .line2(userPostalAddress.getLine2())
                .postalCode(userPostalAddress.getPostalCode())
                .city(userPostalAddress.getCity())
                .country(userPostalAddress.getCountry())
                .build();

    }

    public void setLegalOfficerDecisions(Set<LegalOfficerDecisionDescription> legalOfficerDecisionDescriptions, LocalDateTime createdOn) {
        decisions = legalOfficerDecisionDescriptions.stream()
                .map(decision -> {
                    var legalOfficerDecision = new LegalOfficerDecision();
                    legalOfficerDecision.id = new LegalOfficerDecision.LegalOfficerDecisionId(id, decision.getLegalOfficerAddress());
                    legalOfficerDecision.status = LegalOfficerDecisionStatus.PENDING;
                    legalOfficerDecision.createdOn = createdOn;
                    return legalOfficerDecision;
                })
                .collect(Collectors.toSet());

    }

    public Set<LegalOfficerDecisionDescription> getLegalOfficerDecisionDescriptions() {
        if (decisions == null) {
            return Collections.emptySet();
        }
        return decisions.stream()
                .map(LegalOfficerDecision::getDescription)
                .collect(Collectors.toSet());
    }

    ProtectionRequestAggregateRoot() {
    }
}
