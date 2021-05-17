package logion.backend.model.tokenizationrequest;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizationRequestAggregateRootTest {

    @Test
    void rejectPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenRejecting();
        thenRequestStatusIs(TokenizationRequestStatus.REJECTED);
    }

    private void givenRequestWithStatus(TokenizationRequestStatus status) {
        request = new TokenizationRequestAggregateRoot();
        request.status = status;
    }

    private TokenizationRequestAggregateRoot request;

    private void whenRejecting() {
        request.reject();
    }

    private void thenRequestStatusIs(TokenizationRequestStatus expectedStatus) {
        assertThat(request.getStatus(), equalTo(expectedStatus));
    }

    @Test
    void rejectRejectedThrows() {
        givenRequestWithStatus(TokenizationRequestStatus.REJECTED);
        assertThrows(IllegalStateException.class, this::whenRejecting);
    }
}
