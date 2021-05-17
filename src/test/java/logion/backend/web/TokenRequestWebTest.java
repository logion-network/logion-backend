package logion.backend.web;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import logion.backend.api.TokenRequestController;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestFactory;
import logion.backend.model.tokenizationrequest.TokenizationRequestRepository;
import logion.backend.model.tokenizationrequest.TokenizationRequestStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
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

    @ParameterizedTest
    @MethodSource
    void createTokenRequest(String request, ResultMatcher resultMatcher, int numberOfInvocation, TokenizationRequestDescription expectedTokenDescription) throws Exception {
        var tokenizationRequest = mock(TokenizationRequestAggregateRoot.class);
        when(tokenizationRequest.getDescription()).thenReturn(expectedTokenDescription);

        when(tokenizationRequestFactory.newPendingTokenizationRequest(any(), eq(expectedTokenDescription)))
            .thenReturn(tokenizationRequest);
        when(tokenizationRequestCommands.addTokenizationRequest(tokenizationRequest)).thenReturn(tokenizationRequest);

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
        return validRequest.toString();
    }

    @Test
    void rejectTokenRequest() throws Exception {
        var requestId = UUID.randomUUID();

        mvc.perform(post("/token-request/" + requestId.toString() + "/reject")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tokenizationRequestCommands).rejectTokenizationRequest(requestId);
    }

    @ParameterizedTest
    @MethodSource
    void queryTokenRequests(
            String request,
            Ss58Address legalOfficerAddress,
            List<TokenizationRequestAggregateRoot> tokenizationRequests,
            int expectedResults) throws Exception {
        when(tokeninzationRequestRepository.findByLegalOfficerAddress(legalOfficerAddress)).thenReturn(tokenizationRequests);
        mvc.perform(put("/token-request/")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requests.length()").value(is(expectedResults)));
    }

    @MockBean
    private TokenizationRequestRepository tokeninzationRequestRepository;

    @SuppressWarnings("unused")
    private static Stream<Arguments> queryTokenRequests() throws JSONException {
        return Stream.of(
            Arguments.of(
                queryRequestBody(DefaultAddresses.ALICE, TokenizationRequestStatus.PENDING),
                DefaultAddresses.ALICE,
                aliceRequests(),
                3
            ),
            Arguments.of(
                queryRequestBody(DefaultAddresses.ALICE, TokenizationRequestStatus.REJECTED),
                DefaultAddresses.ALICE,
                aliceRequests(),
                2
            ),
            Arguments.of(
                queryRequestBody(DefaultAddresses.BOB, TokenizationRequestStatus.PENDING),
                DefaultAddresses.BOB,
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

    private static List<TokenizationRequestAggregateRoot> aliceRequests() {
        var requests = new ArrayList<TokenizationRequestAggregateRoot>();
        requests.add(alicePendingRequest(1));
        requests.add(alicePendingRequest(2));
        requests.add(alicePendingRequest(3));
        requests.add(aliceRejectedRequest(4));
        requests.add(aliceRejectedRequest(5));
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

    private static TokenizationRequestAggregateRoot aliceRejectedRequest(int index) {
        return request(TokenizationRequestDescription.builder()
                .legalOfficerAddress(DefaultAddresses.ALICE)
                .requestedTokenName("MYT" + index)
                .requesterAddress(new Ss58Address("requester" + index))
                .bars(index)
                .build(), TokenizationRequestStatus.REJECTED);
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
