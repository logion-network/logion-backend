package logion.backend.sync;

import java.util.ArrayList;
import java.util.Optional;
import logion.backend.chain.DynamicParser;
import logion.backend.chain.view.Block;
import logion.backend.chain.view.Extrinsic;
import logion.backend.chain.view.Method;
import logion.backend.sync.vo.BlockWithTransactions;
import logion.backend.sync.vo.ExtrinsicType;
import logion.backend.sync.vo.TransactionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.stream;

@Service
public class TransactionExtractor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DynamicParser dynamicParser;

    public Optional<BlockWithTransactions> parseBlock(Block block) {
        if (block.getExtrinsics() == null || block.getExtrinsics().length <= 1) {
            return Optional.empty();
        }
        var blockBuilder = BlockWithTransactions.builder()
                .blockNumber(block.getNumber());
        logger.debug("Parsing block {}", block.getNumber());
        var transactions = new ArrayList<TransactionVO>();
        for (int index = 0; index < block.getExtrinsics().length; index++) {
            var extrinsic = block.getExtrinsics()[index];
            var type = determineType(extrinsic);
            if (type == ExtrinsicType.TIMESTAMP) {
                blockBuilder.timestamp(dynamicParser.getTimestamp(extrinsic));
            } else {
                parseExtrinsic(extrinsic, type, block.getNumber(), index).ifPresent(transactions::add);
            }
        }
        if (transactions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(blockBuilder.transactions(transactions).build());
    }

    private Optional<TransactionVO> parseExtrinsic(Extrinsic extrinsic, ExtrinsicType type, long blockNumber, int index) {
        if (!extrinsic.isSuccess()) {
            logger.warn("Block {} - Extrinsic {} is not successful", blockNumber, index);
            return Optional.empty();
        }

        // pallet-method-tip
        var transactionBuilder = TransactionVO.builder()
                .extrinsicIndex(index)
                .pallet(extrinsic.getMethod().getPallet())
                .method(extrinsic.getMethod().getName())
                .tip(nullTo0(extrinsic.getTip()));
        // fee
        var info = extrinsic.getInfo();
        if (info.getError() == null) {
            transactionBuilder.fee(nullTo0(info.getPartialFee()));
        } else {
            logger.warn("Error related to fees: {}", info.getError());
        }

        // from
        transactionBuilder.from(extrinsic.getSignature().getSigner().getId());

        // to
        if (type == ExtrinsicType.TRANSFER) {
            var argBalancesTransfer = dynamicParser.getArgBalancesTransfer(extrinsic);
            transactionBuilder.to(argBalancesTransfer.getDest().getId());
            transactionBuilder.transferValue(argBalancesTransfer.getValue());
        }

        // reserved
        findEventData(extrinsic, new Method("balances", "Reserved"))
                .ifPresent(data -> transactionBuilder.reserved(Long.parseLong(data[1])));

        return Optional.of(transactionBuilder.build());
    }

    private Optional<String[]> findEventData(Extrinsic extrinsic, Method method) {
        return stream(extrinsic.getEvents())
                .filter(event -> event.getMethod().equals(method))
                .findFirst()
                .map(dynamicParser::getData);
    }

    private ExtrinsicType determineType(Extrinsic extrinsic) {
        var method = extrinsic.getMethod();
        switch (method.getPallet()) {
            case "timestamp":
                return ExtrinsicType.TIMESTAMP;

            case "balances":
                return ExtrinsicType.TRANSFER;

            default:
                return ExtrinsicType.GENERIC_TRANSACTION;
        }
    }

    private long nullTo0(Long value) {
        if (value == null) {
            return 0L;
        }
        return value;
    }
}
