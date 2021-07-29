package logion.backend.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import logion.backend.chain.view.Block;
import logion.backend.sync.vo.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
class TransactionExtractorTest {

    @Autowired
    @Qualifier("sidecarObjectMapper")
    private ObjectMapper sidecarObjectMapper;

    @Autowired
    private TransactionExtractor transactionExtractor;

    @Test
    void empty() throws Exception {
        var block = givenBlock("block-empty.json");
        assertThat(transactionExtractor.extractBlockWithTransaction(block).isEmpty(), is(true));
    }

    @Test
    void failure() throws Exception {
        var block = givenBlock("recovery/block-06-recovery-asRecovered-failed.json");
        assertThat(transactionExtractor.extractBlockWithTransaction(block).isEmpty(), is(true));
    }

    @ParameterizedTest
    @MethodSource
    void recovery(String fileName, String method, long blockNumber, long fee, long reserved, String from) throws Exception {
        check(fileName, "recovery", method, blockNumber, fee, reserved, 0L, 0L, from, null);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> recovery() {
        return Stream.of(
                Arguments.of("recovery/block-01-recovery-createRecovery.json", "createRecovery", 119261L, 125000178L, 12L, "5CSbpCKSTvZefZYddesUQ9w6NDye2PHbf12MwBZGBgzGeGoo"),
                Arguments.of("recovery/block-02-recovery-initiateRecovery.json", "initiateRecovery", 119459L, 125000139L, 10L, "5DPPdRwkgigKt2L7jxRfAoV4tfS89KgXsx47Wk3Kat5K6xPg"),
                Arguments.of("recovery/block-03-recovery-vouchRecovery.json", "vouchRecovery", 119506L, 125000171L, 0L, "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"),
                Arguments.of("recovery/block-04-recovery-vouchRecovery.json", "vouchRecovery", 119543L, 125000171L, 0L, "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty"),
                Arguments.of("recovery/block-05-recovery-claimRecovery.json", "claimRecovery", 119581L, 125000139L, 0L, "5DPPdRwkgigKt2L7jxRfAoV4tfS89KgXsx47Wk3Kat5K6xPg"),
                Arguments.of("recovery/block-06-recovery-asRecovered.json", "asRecovered", 3388L, 125000192L, 0L, "5EBxoSssqNo23FvsDeUxjyQScnfEiGxJaNwuwqBH2Twe35BX")
        );
    }

    @ParameterizedTest
    @MethodSource
    void token(String fileName, String method, long blockNumber, long fee, long reserved, String from) throws Exception {
        check(fileName, "assets", method, blockNumber, fee, reserved, 0L, 0L, from, null);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> token() {
        return Stream.of(
                Arguments.of("token/block-01-assets-create.json", "create", 28552L, 125000169L, 11L, "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"),
                Arguments.of("token/block-02-assets-setMetadata.json", "setMetadata", 28570L, 125000141L, 23L, "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"),
                Arguments.of("token/block-03-assets-mint.json", "mint", 28620L, 125000158L, 0L, "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY")
                );
    }

    @ParameterizedTest
    @MethodSource
    void transfer(String fileName, String method, long blockNumber, long fee, long tip, long transferValue, String from, String to) throws Exception {
        check(fileName, "balances", method, blockNumber, fee, 0L, tip, transferValue, from, to);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> transfer() {
        return Stream.of(
                Arguments.of("transfer/block-transfer.json", "transfer", 2223L, 125000149L, 0L, 100000000000000000L, "5DAAnrj7VHTznn2AWBemMuyBwZWs6FNFjdyVXUeYum3PTXFy", "5CSbpCKSTvZefZYddesUQ9w6NDye2PHbf12MwBZGBgzGeGoo"),
                Arguments.of("transfer/block-transferKeepAlive-tip.json", "transferKeepAlive", 59801L, 125000152L, 25000000L, 200000000000000000L, "5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY", "5CSbpCKSTvZefZYddesUQ9w6NDye2PHbf12MwBZGBgzGeGoo"),
                Arguments.of("transfer/block-transferKeepAlive.json", "transferKeepAlive", 1593L, 125000149L, 0L, 540000000000000000L, "5DAAnrj7VHTznn2AWBemMuyBwZWs6FNFjdyVXUeYum3PTXFy", "5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY"),
                Arguments.of("transfer/block-transferKeepAlive2.json", "transferKeepAlive", 1739L, 125000149L, 0L, 200000000000000000L, "5DAAnrj7VHTznn2AWBemMuyBwZWs6FNFjdyVXUeYum3PTXFy", "5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY")
        );
    }

    private void check(String fileName, String pallet, String method, long blockNumber, long fee, long reserved, long tip, long transferValue, String from, String to) throws Exception {
        var block = givenBlock(fileName);
        var blockWithTransactions = transactionExtractor.extractBlockWithTransaction(block).orElseThrow();
        assertThat(blockWithTransactions.getBlockNumber(), is(blockNumber));
        Transaction transaction = blockWithTransactions.getTransactions().get(0);
        assertThat(transaction.getExtrinsicIndex(), is(1));
        assertThat(transaction.getPallet(), is(pallet));
        assertThat(transaction.getMethod(), is(method));
        assertThat(transaction.getFee(), is(fee));
        assertThat(transaction.getReserved(), is(reserved));
        assertThat(transaction.getTip(), is(tip));
        assertThat(transaction.getTransferValue(), is(transferValue));
        assertThat(transaction.getFrom(), is(from));
        assertThat(transaction.getTo(), is(to));
    }

    private Block givenBlock(String fileName) throws Exception {
        URL resource = getClass().getClassLoader().getResource("sidecar/" + fileName);
        assert resource != null;
        var response = Files.readString(Paths.get(resource.getPath()));
        return sidecarObjectMapper.reader().readValue(response, Block.class);
    }
}
