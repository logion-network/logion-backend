package logion.backend.web;

import java.util.UUID;
import java.util.stream.Stream;
import logion.backend.api.TokenRequestController;
import logion.backend.commands.TokenizationRequestCommands;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestFactory;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TokenRequestController.class)
class TokenRequestWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TokenizationRequestFactory tokenRequestFactory;

    @MockBean
    private TokenizationRequestCommands tokenizationRequestCommands;

    @ParameterizedTest
    @MethodSource
    void createTokenRequest(String request, ResultMatcher resultMatcher, int numberOfInvocation, TokenizationRequestDescription expectedTokenDescription) throws Exception {
        var tokenizationRequest = mock(TokenizationRequestAggregateRoot.class);
        when(tokenizationRequest.getDescription()).thenReturn(expectedTokenDescription);

        when(tokenRequestFactory.newPendingTokenizationRequest(any(), eq(expectedTokenDescription)))
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
}
