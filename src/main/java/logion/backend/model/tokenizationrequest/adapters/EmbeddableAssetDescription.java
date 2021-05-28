package logion.backend.model.tokenizationrequest.adapters;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
public class EmbeddableAssetDescription {

    @Getter
    @Setter
    String assetId;

    @Getter
    @Setter
    Integer decimals;
}
