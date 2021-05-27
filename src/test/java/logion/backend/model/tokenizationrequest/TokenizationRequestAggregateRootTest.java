package logion.backend.model.tokenizationrequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizationRequestAggregateRootTest {

    private static final String REJECT_REASON = "Illegal";
    private static final LocalDateTime REJECTED_ON = LocalDateTime.now();
    private static final LocalDateTime ACCEPTED_ON = REJECTED_ON.plusMinutes(1);

    @Test
    void rejectPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenRejecting(REJECT_REASON, REJECTED_ON);
        thenRequestStatusIs(TokenizationRequestStatus.REJECTED);
        thenRequestRejectReasonIs(REJECT_REASON);
        thenDecisionOnIs(REJECTED_ON);
    }

    @Test
    void acceptPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenAccepting(ACCEPTED_ON);
        thenRequestStatusIs(TokenizationRequestStatus.ACCEPTED);
        thenRequestRejectReasonIs(null);
        thenDecisionOnIs(ACCEPTED_ON);
    }

    private void givenRequestWithStatus(TokenizationRequestStatus status) {
        request = new TokenizationRequestAggregateRoot();
        request.status = status;
    }

    private TokenizationRequestAggregateRoot request;

    private void whenRejecting(String rejectReason, LocalDateTime rejectedOn) {
        request.reject(rejectReason, rejectedOn);
    }

    private void whenAccepting(LocalDateTime acceptedOn) {
        request.accept(acceptedOn);
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

    @ParameterizedTest
    @MethodSource("statusForDecidedRequest")
    void rejectDecidedRequestThrows(TokenizationRequestStatus status) {
        givenRequestWithStatus(status);
        assertThrows(IllegalStateException.class, () -> whenRejecting(REJECT_REASON, REJECTED_ON));
    }

    @ParameterizedTest
    @MethodSource("statusForDecidedRequest")
    void acceptDecidedRequestThrows(TokenizationRequestStatus status) {
        givenRequestWithStatus(status);
        assertThrows(IllegalStateException.class, () -> whenAccepting(ACCEPTED_ON));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> statusForDecidedRequest() {
        return Stream.of(
                Arguments.of(TokenizationRequestStatus.ACCEPTED),
                Arguments.of(TokenizationRequestStatus.REJECTED)
        );
    }
}
