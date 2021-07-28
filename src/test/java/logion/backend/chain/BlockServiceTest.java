package logion.backend.chain;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import logion.backend.chain.view.Block;
import logion.backend.chain.view.BlockNumber;
import logion.backend.chain.view.Extrinsic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
class BlockServiceTest {

    @Autowired
    private BlockService blockService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @ParameterizedTest
    @MethodSource
    void getBlock(String responseFile, long blockNumber) throws Exception {

        // Given
        var response = givenResponse(responseFile);
        initMockServer(blockNumber, response);

        // When
        Block block = blockService.getBlock(blockNumber);

        // Then
        mockServer.verify();
        assertThat(block.getNumber(), is(blockNumber));
        Extrinsic extrinsic = block.getExtrinsics()[0];
        assertThat(extrinsic.getMethod().getPallet(), is("timestamp"));
        assertThat(extrinsic.getMethod().getName(), is("set"));
    }

    @ParameterizedTest
    @MethodSource("getBlock")
    void getHeadBlockNumber(String responseFile, long blockNumber) throws Exception {

        // Given
        var response = givenResponse(responseFile);
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://127.0.0.1:8081/blocks/head")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        // When
        long actualBlockNumber = blockService.getHeadBlockNumber();

        // Then
        mockServer.verify();
        assertThat(actualBlockNumber, is(blockNumber));
    }

    private void initMockServer(long blockNumber, String response) throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://127.0.0.1:8081/blocks/" + blockNumber)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));
    }

    private String givenResponse(String responseFile) throws Exception {
        URL resource = getClass().getClassLoader().getResource("sidecar/" + responseFile);
        assert resource != null;
        return Files.readString(Paths.get(resource.getPath()));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> getBlock() {
        return Stream.of(
                Arguments.of("block-empty.json", 1592L),

                Arguments.of("token/block-01-assets-create.json", 28552L),
                Arguments.of("token/block-02-assets-setMetadata.json", 28570L),
                Arguments.of("token/block-03-assets-mint.json", 28620L),

                Arguments.of("recovery/block-01-recovery-createRecovery.json", 119261L),
                Arguments.of("recovery/block-02-recovery-initiateRecovery.json", 119459L),
                Arguments.of("recovery/block-03-recovery-vouchRecovery.json", 119506L),
                Arguments.of("recovery/block-04-recovery-vouchRecovery.json", 119543L),
                Arguments.of("recovery/block-05-recovery-claimRecovery.json", 119581L),

                Arguments.of("transfer/block-transfer.json", 2223L),
                Arguments.of("transfer/block-transferKeepAlive-tip.json", 59801L),
                Arguments.of("transfer/block-transferKeepAlive.json", 1593L),
                Arguments.of("transfer/block-transferKeepAlive2.json", 1739L)
                );
    }
}
