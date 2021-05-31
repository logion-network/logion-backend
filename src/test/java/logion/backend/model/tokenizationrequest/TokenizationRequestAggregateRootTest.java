package logion.backend.model.tokenizationrequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import logion.backend.crypto.Hash;
import logion.backend.crypto.Hashing;
import logion.backend.model.tokenizationrequest.adapters.EmbeddableAssetDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizationRequestAggregateRootTest {

    private static final String REJECT_REASON = "Illegal";
    private static final LocalDateTime REJECTED_ON = LocalDateTime.now();
    private static final LocalDateTime ACCEPTED_ON = REJECTED_ON.plusMinutes(1);
    private static final Hash SESSION_TOKEN = Hashing.sha256("token");

    @Test
    void rejectPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenRejecting(REJECT_REASON, REJECTED_ON);
        thenRequestStatusIs(TokenizationRequestStatus.REJECTED);
        thenRequestRejectReasonIs(REJECT_REASON);
        thenDecisionOnIs(REJECTED_ON);
        thenAcceptSessionTokenIsAvailable(false);
    }

    @Test
    void acceptPending() {
        givenRequestWithStatus(TokenizationRequestStatus.PENDING);
        whenAccepting(ACCEPTED_ON, SESSION_TOKEN);
        thenRequestStatusIs(TokenizationRequestStatus.ACCEPTED);
        thenRequestRejectReasonIs(null);
        thenDecisionOnIs(ACCEPTED_ON);
        thenAcceptSessionTokenIsAvailable(true);
    }

    private void givenRequestWithStatus(TokenizationRequestStatus status) {
        request = new TokenizationRequestAggregateRoot();
        request.status = status;
    }

    private TokenizationRequestAggregateRoot request;

    private void whenRejecting(String rejectReason, LocalDateTime rejectedOn) {
        request.reject(rejectReason, rejectedOn);
    }

    private void whenAccepting(LocalDateTime acceptedOn, Hash sessionToken) {
        request.accept(acceptedOn, sessionToken);
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

    private void thenAcceptSessionTokenIsAvailable(boolean expected) {
        if(expected) {
            assertThat(request.acceptSessionTokenHash, notNullValue());
        } else {
            assertThat(request.acceptSessionTokenHash, nullValue());
        }
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
        assertThrows(IllegalStateException.class, () -> whenAccepting(ACCEPTED_ON, SESSION_TOKEN));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> statusForDecidedRequest() {
        return Stream.of(
                Arguments.of(TokenizationRequestStatus.ACCEPTED),
                Arguments.of(TokenizationRequestStatus.REJECTED)
        );
    }

    @Test
    void nullEmbeddableThenEmptyAssetDescription() {
        givenRequestWithEmbeddableAssetDescription(null);
        assertThat(request.getAssetDescription(), is(Optional.empty()));
    }

    private void givenRequestWithEmbeddableAssetDescription(EmbeddableAssetDescription description) {
        request = new TokenizationRequestAggregateRoot();
        request.assetDescription = description;
    }

    @Test
    void emptyEmbeddableThenEmptyAssetDescription() {
        givenRequestWithEmbeddableAssetDescription(emptyDescription());
        assertThat(request.getAssetDescription(), is(Optional.empty()));
    }

    private EmbeddableAssetDescription emptyDescription() {
        return new EmbeddableAssetDescription();
    }

    @Test
    void setAssetDescriptionClearsSessionToken() {
        givenRequestWithStatus(TokenizationRequestStatus.ACCEPTED);
        givenAcceptSessionToken("token");
        whenSettingAssetDescription("token", AssetDescription.builder()
                .assetId(new AssetId("assetId"))
                .decimals(10)
                .build());
        assertThat(request.acceptSessionTokenHash, nullValue());
    }

    private void givenAcceptSessionToken(String token) {
        request.acceptSessionTokenHash = Hashing.sha256(token).toBase64();
    }

    private void whenSettingAssetDescription(String sessionToken, AssetDescription description) {
        request.setAssetDescription(Hashing.sha256(sessionToken), description);
    }

    @Test
    void setAssetDescriptionWithWrongSessionTokenFails() {
        givenRequestWithStatus(TokenizationRequestStatus.ACCEPTED);
        givenAcceptSessionToken("token");
        var description = AssetDescription.builder()
                .assetId(new AssetId("assetId"))
                .decimals(10)
                .build();
        assertThrows(IllegalArgumentException.class, () -> whenSettingAssetDescription("other-token", description));
    }
}
