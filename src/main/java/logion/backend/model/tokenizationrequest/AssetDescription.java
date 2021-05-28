package logion.backend.model.tokenizationrequest;

import logion.backend.annotation.ValueObject;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@ValueObject
public class AssetDescription {

    private AssetId assetId;

    private int decimals;
}
