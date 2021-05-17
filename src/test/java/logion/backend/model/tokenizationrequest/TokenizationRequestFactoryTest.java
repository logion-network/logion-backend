package logion.backend.model.tokenizationrequest;

import java.util.UUID;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class TokenizationRequestFactoryTest {

    @Test
    void createsPendingRequests() {
        givenRequestId(UUID.randomUUID());
        TokenizationRequestDescription description = TokenizationRequestDescription.builder()
                .requesterAddress(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"))
                .requestedTokenName("MYT")
                .bars(1)
                .build();
        givenTokenDescription(description);
        whenCreatingTokenizationRequest();
        thenPendingRequestCreatedWithDescription(description);
    }

    private void givenRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    private UUID requestId;

    private void givenTokenDescription(TokenizationRequestDescription description) {
        tokenDescription = description;
    }

    private TokenizationRequestDescription tokenDescription;

    private void whenCreatingTokenizationRequest() {
        createdTokenizationRequest = factory.newPendingTokenizationRequest(requestId, tokenDescription);
    }

    private TokenizationRequestFactory factory = new TokenizationRequestFactory();

    private TokenizationRequestAggregateRoot createdTokenizationRequest;

    private void thenPendingRequestCreatedWithDescription(TokenizationRequestDescription description) {
        assertThat(createdTokenizationRequest.getId(), equalTo(requestId));
        assertThat(createdTokenizationRequest.getDescription(), equalTo(description));
    }
}
