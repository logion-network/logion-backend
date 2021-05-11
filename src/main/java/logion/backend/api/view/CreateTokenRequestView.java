package logion.backend.api.view;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateTokenRequestView {

    String tokenName;
    String userAccount;
    int numberOfGoldBars;

}
