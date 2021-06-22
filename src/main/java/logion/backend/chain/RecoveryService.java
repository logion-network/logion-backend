package logion.backend.chain;

import java.util.Optional;
import logion.backend.chain.view.PalletStorageItem;
import logion.backend.chain.view.RecoveryConfig;
import logion.backend.model.Ss58Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecoveryService {

    @Autowired
    private RestTemplate sidecar;

    public Optional<RecoveryConfig> getRecoveryConfig(Ss58Address userAddress) {
        var url = "/pallets/recovery/storage/Recoverable?key1={userAddress}";
        var palletStorageItem = sidecar.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PalletStorageItem<RecoveryConfig>>() {},
                userAddress.getRawValue());

        return Optional.ofNullable(palletStorageItem.getBody())
                .map(PalletStorageItem::getValue);
    }
}
