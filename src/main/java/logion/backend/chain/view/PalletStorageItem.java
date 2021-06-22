package logion.backend.chain.view;

import logion.backend.annotation.View;
import lombok.Value;

@View
@Value
public class PalletStorageItem<T> {

    String pallet;
    String storageItem;
    T value;
}
