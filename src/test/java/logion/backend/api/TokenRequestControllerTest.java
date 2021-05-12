package logion.backend.api;

import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenRequestControllerTest {

    private final TokenRequestController tokenRequestController = new TokenRequestController();

    @Test
    void createTokenRequest() {
        // Given
        CreateTokenRequestView createTokenRequestView = CreateTokenRequestView.builder()
                .tokenName("MyFavoriteToken")
                .userAccount("MyAccount")
                .numberOfGoldBars(40)
                .build();
        // When
        TokenRequestView tokenRequest = tokenRequestController.createTokenRequest(createTokenRequestView);
        // Then
        assertNotNull(tokenRequest);
        assertNotNull(tokenRequest.getId());
    }
}
