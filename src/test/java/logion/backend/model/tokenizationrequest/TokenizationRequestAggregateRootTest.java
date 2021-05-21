package logion.backend.model.tokenizationrequest;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizationRequestAggregateRootTest {

    private static final String REJECT_REASON = "Illegal";

    @Test
    void rejectPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenRejecting(REJECT_REASON);
        thenRequestStatusIs(TokenizationRequestStatus.REJECTED);
        thenRequestRejectReasonIs(REJECT_REASON);
    }

    private void givenRequestWithStatus(TokenizationRequestStatus status) {
        request = new TokenizationRequestAggregateRoot();
        request.status = status;
    }

    private TokenizationRequestAggregateRoot request;

    private void whenRejecting(String rejectReason) {
        request.reject(rejectReason);
    }

    private void thenRequestStatusIs(TokenizationRequestStatus expectedStatus) {
        assertThat(request.getStatus(), equalTo(expectedStatus));
    }

    private void thenRequestRejectReasonIs(String rejectReason) {
        assertThat(request.getRejectReason(), equalTo(rejectReason));
    }

    @Test
    void rejectRejectedThrows() {
        givenRequestWithStatus(TokenizationRequestStatus.REJECTED);
        assertThrows(IllegalStateException.class, () -> whenRejecting(REJECT_REASON));
    }
}
