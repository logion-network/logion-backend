package logion.backend.chain;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import logion.backend.chain.view.RecoveryConfig;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
class RecoveryServiceTest {

    @Autowired
    private RecoveryService recoveryService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @ParameterizedTest
    @MethodSource
    void getRecoveryConfig(String responseFile, String address, boolean recoveryConfigExists) throws Exception {

        // Given
        URL resource = getClass().getClassLoader().getResource("sidecar/" + responseFile);
        assert resource != null;
        var response = Files.readString(Paths.get(resource.getPath()));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://127.0.0.1:8081/pallets/recovery/storage/Recoverable?key1=" + address)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        // When
        Optional<RecoveryConfig> recoveryConfig = recoveryService.getRecoveryConfig(new Ss58Address(address));

        // Then
        mockServer.verify();
        assertThat(recoveryConfig.isPresent(), is(recoveryConfigExists));
        recoveryConfig.ifPresent(config -> assertThat(config.getFriends(), is(new String[]{
                "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty",
                "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"})));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> getRecoveryConfig() {
        return Stream.of(
                Arguments.of("recoveryConfig.json", "5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW", true),
                Arguments.of("noRecoveryConfig.json", "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty", false)
        );
    }
}
