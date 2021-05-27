package logion.backend.model.tokenizationrequest;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizationRequestAggregateRootTest {

    private static final String REJECT_REASON = "Illegal";
    private static final LocalDateTime REJECTED_ON = LocalDateTime.now();

    @Test
    void rejectPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenRejecting(REJECT_REASON, REJECTED_ON);
        thenRequestStatusIs(TokenizationRequestStatus.REJECTED);
        thenRequestRejectReasonIs(REJECT_REASON);
        thenDecisionOnIs(REJECTED_ON);
    }

    private void givenRequestWithStatus(TokenizationRequestStatus status) {
        request = new TokenizationRequestAggregateRoot();
        request.status = status;
    }

    private TokenizationRequestAggregateRoot request;

    private void whenRejecting(String rejectReason, LocalDateTime rejectedOn) {
        request.reject(rejectReason, rejectedOn);
    }

    private void thenRequestStatusIs(TokenizationRequestStatus expectedStatus) {
        assertThat(request.getStatus(), equalTo(expectedStatus));
    }

    private void thenRequestRejectReasonIs(String rejectReason) {
        assertThat(request.getRejectReason(), equalTo(rejectReason));
    }

    private void thenDecisionOnIs(LocalDateTime rejectedOn) {
        assertThat(request.getDecisionOn(), equalTo(rejectedOn));
    }

    @Test
    void rejectRejectedThrows() {
        givenRequestWithStatus(TokenizationRequestStatus.REJECTED);
        assertThrows(IllegalStateException.class, () -> whenRejecting(REJECT_REASON, REJECTED_ON));
    }
}
