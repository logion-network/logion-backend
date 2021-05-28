package logion.backend.model.tokenizationrequest;

import logion.backend.annotation.ValueObject;
import lombok.Value;

@Value
@ValueObject
public class AssetId {

    private String value;
}
