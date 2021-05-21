package logion.backend.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import logion.backend.api.TokenRequestController;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import logion.backend.model.Subkey;
import logion.backend.model.tokenizationrequest.FetchRequestsSpecification;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestFactory;
import logion.backend.model.tokenizationrequest.TokenizationRequestRepository;
import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import logion.backend.subkey.SubkeyWrapper;
import logion.backend.subkey.SubkeyWrapper.ExpectingAddress;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TokenRequestController.class)
class TokenRequestWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TokenizationRequestFactory tokenizationRequestFactory;

    @MockBean
    private TokenizationRequestCommands tokenizationRequestCommands;

    @MockBean
    private Subkey subkey;

    @ParameterizedTest
    @MethodSource
    void createTokenRequest(String request, ResultMatcher resultMatcher, int numberOfInvocation, TokenizationRequestDescription expectedTokenDescription) throws Exception {
        var tokenizationRequest = mock(TokenizationRequestAggregateRoot.class);
        when(tokenizationRequest.getDescription()).thenReturn(expectedTokenDescription);

        when(tokenizationRequestFactory.newPendingTokenizationRequest(any(), eq(expectedTokenDescription)))
            .thenReturn(tokenizationRequest);
        when(tokenizationRequestCommands.addTokenizationRequest(tokenizationRequest)).thenReturn(tokenizationRequest);

        if(expectedTokenDescription != null) {
            var message =  new StringBuilder()
                    .append(expectedTokenDescription.getLegalOfficerAddress().getRawValue())
                    .append('-')
                    .append(expectedTokenDescription.getRequesterAddress().getRawValue())
                    .append('-')
                    .append(expectedTokenDescription.getRequestedTokenName())
                    .append('-')
                    .append(expectedTokenDescription.getBars())
                    .toString();
            var approving = signatureVerifyMock(message, expectedTokenDescription.getRequesterAddress(), true);
            when(subkey.verify("signature")).thenReturn(approving);
        }

        mvc.perform(post("/token-request")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(resultMatcher);

        verify(tokenizationRequestCommands, times(numberOfInvocation)).addTokenizationRequest(tokenizationRequest);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTokenRequest() throws JSONException {
        var tokenDescription = TokenizationRequestDescription.builder()
                .requestedTokenName("MYT")
                .legalOfficerAddress(DefaultAddresses.ALICE)
                .requesterAddress(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"))
                .bars(1)
                .build();
        return Stream.of(
                Arguments.of(validRequest(), status().isOk(), 1, tokenDescription),
                Arguments.of("", status().isBadRequest(), 0, null)
        );
    }

    private static String validRequest() throws JSONException {
        var validRequest = new JSONObject();
        validRequest.put("requestedTokenName", "MYT");
        validRequest.put("legalOfficerAddress", DefaultAddresses.ALICE.getRawValue());
        validRequest.put("requesterAddress", "5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
        validRequest.put("bars", 1);
        validRequest.put("signature", "signature");
        return validRequest.toString();
    }

    @ParameterizedTest
    @MethodSource
    void rejectTokenRequestWithWrongSignature(boolean signatureVerifyResult, ResultMatcher matcher) throws Exception {
        var requestId = UUID.randomUUID();
        var requestBody = new JSONObject();
        requestBody.put("legalOfficerAddress", DefaultAddresses.ALICE.getRawValue());
        requestBody.put("signature", SIGNATURE);
        requestBody.put("rejectReason", REJECT_REASON);

        var message = DefaultAddresses.ALICE.getRawValue() + "-" + requestId + "-" + REJECT_REASON;
        var approving = signatureVerifyMock(message, DefaultAddresses.ALICE, signatureVerifyResult);
        when(subkey.verify("signature")).thenReturn(approving);

        mvc.perform(post("/token-request/" + requestId.toString() + "/reject")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(requestBody.toString()))
                .andExpect(matcher);

        if(signatureVerifyResult) {
            verify(tokenizationRequestCommands).rejectTokenizationRequest(requestId, REJECT_REASON);
        }
    }

    private static final String SIGNATURE = "signature";
    private static final String REJECT_REASON = "Illegal";

    private ExpectingAddress signatureVerifyMock(String message, Ss58Address address, boolean verifyResult) {
        var expectingMessage = mock(SubkeyWrapper.ExpectingAddress.ExpectingMessage.class);
        when(expectingMessage.withMessage(message)).thenReturn(verifyResult);

        var expectingAddress = mock(SubkeyWrapper.ExpectingAddress.class);
        when(expectingAddress.withSs58Address(address)).thenReturn(expectingMessage);

        return expectingAddress;
    }

    private static Stream<Arguments> rejectTokenRequestWithWrongSignature() {
        return Stream.of(
            Arguments.of(true, status().isOk()),
            Arguments.of(false, status().isBadRequest())
        );
    }

    @ParameterizedTest
    @MethodSource
    void queryTokenRequests(
            String request,
            FetchRequestsSpecification query,
            List<TokenizationRequestAggregateRoot> tokenizationRequests,
            int expectedResults) throws Exception {
        when(tokenizationRequestRepository.findBy(query)).thenReturn(tokenizationRequests);
        mvc.perform(put("/token-request/")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requests.length()").value(is(expectedResults)));
    }

    @MockBean
    private TokenizationRequestRepository tokenizationRequestRepository;

    @SuppressWarnings("unused")
    private static Stream<Arguments> queryTokenRequests() throws JSONException {
        return Stream.of(
            Arguments.of(
                queryRequestBody(DefaultAddresses.ALICE, TokenizationRequestStatus.PENDING),
                FetchRequestsSpecification.builder()
                    .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                    .expectedRequesterAddress(Optional.empty())
                    .expectedStatus(TokenizationRequestStatus.PENDING)
                    .build(),
                alicePendingRequests(),
                3
            ),
            Arguments.of(
                queryRequestBody(DefaultAddresses.ALICE, TokenizationRequestStatus.REJECTED),
                FetchRequestsSpecification.builder()
                    .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                    .expectedRequesterAddress(Optional.empty())
                    .expectedStatus(TokenizationRequestStatus.REJECTED)
                    .build(),
                aliceRejectedRequests(),
                2
            ),
            Arguments.of(
                queryRequestBody(DefaultAddresses.BOB, TokenizationRequestStatus.PENDING),
                FetchRequestsSpecification.builder()
                    .expectedLegalOfficer(Optional.of(DefaultAddresses.BOB))
                    .expectedRequesterAddress(Optional.empty())
                    .expectedStatus(TokenizationRequestStatus.PENDING)
                    .build(),
                bobRequests(),
                1
            )
        );
    }

    private static String queryRequestBody(Ss58Address legalOfficerAddress, TokenizationRequestStatus status) throws JSONException {
        var validRequest = new JSONObject();
        validRequest.put("legalOfficerAddress", legalOfficerAddress.getRawValue());
        validRequest.put("status", status);
        return validRequest.toString();
    }

    private static List<TokenizationRequestAggregateRoot> alicePendingRequests() {
        var requests = new ArrayList<TokenizationRequestAggregateRoot>();
        requests.add(alicePendingRequest(1));
        requests.add(alicePendingRequest(2));
        requests.add(alicePendingRequest(3));
        return requests;
    }

    private static TokenizationRequestAggregateRoot alicePendingRequest(int index) {
        return request(TokenizationRequestDescription.builder()
                .legalOfficerAddress(DefaultAddresses.ALICE)
                .requestedTokenName("MYT" + index)
                .requesterAddress(new Ss58Address("requester" + index))
                .bars(index)
                .build(), TokenizationRequestStatus.PENDING);
    }

    private static TokenizationRequestAggregateRoot request(TokenizationRequestDescription description,
            TokenizationRequestStatus status) {
        var request = mock(TokenizationRequestAggregateRoot.class);
        when(request.getDescription()).thenReturn(description);
        when(request.getStatus()).thenReturn(status);
        return request;
    }

    private static TokenizationRequestAggregateRoot rejectedRequest(TokenizationRequestDescription description,
            String rejectReason) {
        var request = request(description, TokenizationRequestStatus.REJECTED);
        when(request.getRejectReason()).thenReturn(rejectReason);
        return request;
    }

    private static List<TokenizationRequestAggregateRoot> aliceRejectedRequests() {
        var requests = new ArrayList<TokenizationRequestAggregateRoot>();
        requests.add(aliceRejectedRequest(4));
        requests.add(aliceRejectedRequest(5));
        return requests;
    }

    private static TokenizationRequestAggregateRoot aliceRejectedRequest(int index) {
        return rejectedRequest(TokenizationRequestDescription.builder()
                .legalOfficerAddress(DefaultAddresses.ALICE)
                .requestedTokenName("MYT" + index)
                .requesterAddress(new Ss58Address("requester" + index))
                .bars(index)
                .build(), "Illegal" + index);
    }

    private static List<TokenizationRequestAggregateRoot> bobRequests() {
        var requests = new ArrayList<TokenizationRequestAggregateRoot>();
        requests.add(bobPendingRequest(1));
        return requests;
    }

    private static TokenizationRequestAggregateRoot bobPendingRequest(int index) {
        return request(TokenizationRequestDescription.builder()
                .legalOfficerAddress(DefaultAddresses.BOB)
                .requestedTokenName("MYT" + index)
                .requesterAddress(new Ss58Address("requester" + index))
                .bars(index)
                .build(), TokenizationRequestStatus.PENDING);
    }
}
