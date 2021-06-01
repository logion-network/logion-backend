package logion.backend.model.tokenizationrequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import logion.backend.annotation.AggregateRoot;
import logion.backend.crypto.Hash;
import logion.backend.model.Ss58Address;
import logion.backend.model.adapters.Ss58AddressConverter;
import logion.backend.model.tokenizationrequest.adapters.EmbeddableAssetDescription;
import lombok.Getter;

@AggregateRoot
@Entity(name = "tokenization_request")
public class TokenizationRequestAggregateRoot {

    public void reject(String reason, LocalDateTime rejectedOn) {
        if(status != TokenizationRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject non-pending request");
        }

        status = TokenizationRequestStatus.REJECTED;
        rejectReason = reason;
        decisionOn = rejectedOn;
    }

    public void accept(LocalDateTime acceptedOn, Hash sessionTokenHash) {
        if(status != TokenizationRequestStatus.PENDING) {
            throw new IllegalStateException("Cannot accept non-pending request");
        }

        status = TokenizationRequestStatus.ACCEPTED;
        decisionOn = acceptedOn;
        acceptSessionTokenHash = sessionTokenHash.toBase64();
    }

    public TokenizationRequestDescription getDescription() {
        return TokenizationRequestDescription.builder()
                .legalOfficerAddress(legalOfficerAddress)
                .requesterAddress(requesterAddress)
                .requestedTokenName(requestedTokenName)
                .bars(bars)
                .createdOn(createdOn)
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

    LocalDateTime createdOn;

    @Getter
    LocalDateTime decisionOn;

    public void setAssetDescription(Hash sessionToken, AssetDescription description) {
        if(acceptSessionTokenHash == null || !acceptSessionTokenHash.equals(sessionToken.toBase64())) {
            throw new IllegalArgumentException("Invalid session token");
        }

        acceptSessionTokenHash = null;
        assetDescription = new EmbeddableAssetDescription();
        assetDescription.setAssetId(description.getAssetId().getValue());
        assetDescription.setDecimals(description.getDecimals());
    }

    String acceptSessionTokenHash;

    @Embedded
    EmbeddableAssetDescription assetDescription;

    public Optional<AssetDescription> getAssetDescription() {
        if(assetDescription == null || assetDescription.getAssetId() == null) {
            return Optional.empty();
        } else {
            return Optional.of(AssetDescription.builder()
                    .assetId(new AssetId(assetDescription.getAssetId()))
                    .decimals(assetDescription.getDecimals())
                    .build());
        }
    }

    TokenizationRequestAggregateRoot() {

    }
}
