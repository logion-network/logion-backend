package logion.backend.web;

import logion.backend.api.TokenRequestController;
import logion.backend.api.view.CreateTokenRequestView;
import logion.backend.api.view.TokenRequestView;
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

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TokenRequestController.class)
public class TokenRequestWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TokenRequestController tokenRequestController;

    @ParameterizedTest
    @MethodSource
    public void createTokenRequest(String request, ResultMatcher resultMatcher, int numberOfInvocation) throws Exception {
        when(tokenRequestController.createTokenRequest(any(CreateTokenRequestView.class)))
                .thenReturn(TokenRequestView.builder()
                        .id(UUID.randomUUID())
                        .build());

        mvc.perform(post("/token-request")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(resultMatcher);

        verify(tokenRequestController, times(numberOfInvocation)).createTokenRequest(any(CreateTokenRequestView.class));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createTokenRequest() {
        String validRequest = "{\"tokenName\": \"MyToken\", \"userAccount\": \"MyAccount\", \"numberOfGoldBars\": \"55\"}";

        return Stream.of(
                Arguments.of(validRequest, status().isOk(), 1),
                Arguments.of("", status().isBadRequest(), 0)
        );
    }
}
