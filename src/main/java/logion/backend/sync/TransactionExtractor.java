package logion.backend.sync;

import java.util.ArrayList;
import java.util.Optional;
import logion.backend.chain.ExtrinsicDataExtractor;
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
    private ExtrinsicDataExtractor extrinsicDataExtractor;

    public Optional<BlockWithTransactions> extractBlockWithTransaction(Block block) {
        if (block.getExtrinsics() == null || block.getExtrinsics().length <= 1) {
            return Optional.empty();
        }
        var blockBuilder = BlockWithTransactions.builder()
                .blockNumber(block.getNumber());
        logger.debug("Looking at block {}", block.getNumber());
        var transactions = new ArrayList<TransactionVO>();
        for (int index = 0; index < block.getExtrinsics().length; index++) {
            var extrinsic = block.getExtrinsics()[index];
            var type = determineType(extrinsic);
            if (type == ExtrinsicType.TIMESTAMP) {
                blockBuilder.timestamp(extrinsicDataExtractor.getTimestamp(extrinsic));
            } else {
                extractTransaction(extrinsic, type, block.getNumber(), index).ifPresent(transactions::add);
            }
        }
        if (transactions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(blockBuilder.transactions(transactions).build());
    }

    private Optional<TransactionVO> extractTransaction(Extrinsic extrinsic, ExtrinsicType type, long blockNumber, int index) {
        if (!extrinsic.isSuccess()) {
            logger.debug("Block {} - Extrinsic {} is not successful", blockNumber, index);
            return Optional.empty();
        }

        var transactionBuilder = TransactionVO.builder()
                .extrinsicIndex(index)
                .pallet(pallet(extrinsic))
                .method(methodName(extrinsic))
                .tip(tip(extrinsic))
                .fee(fee(extrinsic))
                .reserved(reserved(extrinsic))
                .from(from(extrinsic))
                ;
        if (type == ExtrinsicType.TRANSFER) {
            transactionBuilder
                    .transferValue(transferValue(extrinsic))
                    .to(to(extrinsic));
        }

        return Optional.of(transactionBuilder.build());
    }

    private String pallet(Extrinsic extrinsic) {
        return extrinsic.getMethod().getPallet();
    }

    private String methodName(Extrinsic extrinsic) {
        return extrinsic.getMethod().getName();
    }

    private long tip(Extrinsic extrinsic) {
        return nullTo0(extrinsic.getTip());
    }

    private long fee(Extrinsic extrinsic) {
        var info = extrinsic.getInfo();
        if (info.getError() == null) {
            return nullTo0(info.getPartialFee());
        } else {
            logger.warn("Error related to fees: {}", info.getError());
            return 0L;
        }
    }

    private long reserved(Extrinsic extrinsic) {
        return findEventData(extrinsic, new Method("balances", "Reserved"))
                .map(data -> Long.parseLong(data[1]))
                .orElse(0L);
    }

    private String from(Extrinsic extrinsic) {
        return extrinsic.getSignature().getSigner().getId();
    }

    private String to(Extrinsic extrinsic) {
        var argBalancesTransfer = extrinsicDataExtractor.getArgBalancesTransfer(extrinsic);
        return argBalancesTransfer.getDest().getId();
    }

    private long transferValue(Extrinsic extrinsic) {
        var argBalancesTransfer = extrinsicDataExtractor.getArgBalancesTransfer(extrinsic);
        return argBalancesTransfer.getValue();
    }

    private Optional<String[]> findEventData(Extrinsic extrinsic, Method method) {
        return stream(extrinsic.getEvents())
                .filter(event -> event.getMethod().equals(method))
                .findFirst()
                .map(extrinsicDataExtractor::getData);
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
