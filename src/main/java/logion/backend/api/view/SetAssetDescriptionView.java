package logion.backend.api.view;

import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
public class SetAssetDescriptionView {

    AssetDescriptionView description;
    String sessionToken;
}
