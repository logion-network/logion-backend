package logion.backend.api;

import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.TokenRequestView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenRequestControllerTest {

    private final TokenRequestController tokenRequestController = new TokenRequestController();

    @Test
    public void createTokenRequest() {
        // Given
        CreateTokenRequestView createTokenRequestView = new CreateTokenRequestView("MyFavoriteToken", "MyAccount", 40);
        // When
        TokenRequestView tokenRequest = tokenRequestController.createTokenRequest(createTokenRequestView);
        // Then
        assertNotNull(tokenRequest);
        assertNotNull(tokenRequest.getId());
    }
}
