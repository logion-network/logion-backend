package logion.backend.sync;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import logion.backend.chain.BlockService;
import logion.backend.commands.TransactionCommands;
import logion.backend.model.Ss58Address;
import logion.backend.model.sync.SyncPointRepository;
import logion.backend.model.transaction.TransactionDescription;
import logion.backend.model.transaction.TransactionFactory;
import logion.backend.sync.vo.BlockWithTransactions;
import logion.backend.sync.vo.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionSync {

    @Autowired
    private BlockService blockService;

    @Autowired
    private TransactionExtractor transactionExtractor;

    @Autowired
    private TransactionFactory transactionFactory;

    @Autowired
    private TransactionCommands transactionCommands;

    @Autowired
    private SyncPointRepository syncPointRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(initialDelayString = "${logion.sync.transactions.initial-delay:1000}",
            fixedDelayString = "${logion.sync.transactions.delay:3000}")
    public void syncTransactions() {
        long head = blockService.getHeadBlockNumber();
        long lastSynced = lastSyncedBlock();
        if (lastSynced == head) {
            return;
        }
        if (lastSynced > head) {
            logger.warn("Out-of-sync error: last synced block number greater than head number. Transaction cache will be erased and rebuilt from block #1");
            transactionCommands.deleteAllTransactions();
            lastSynced = 0L;
        }
        for (long blockNumber = lastSynced + 1; blockNumber <= head; blockNumber++) {
            if ((blockNumber % 1000L) == 0L  ) {
                logger.debug("Scanning block {}/{}", blockNumber, head);
            }
            processBlock(blockNumber);
        }
    }

    private void processBlock(long blockNumber) {
        var block = blockService.getBlock(blockNumber);
        Optional<BlockWithTransactions> blockWithTransactions = transactionExtractor.extractBlockWithTransaction(block);
        if (blockWithTransactions.isEmpty()) {
            transactionCommands.updateLastProcessedBlock(blockNumber);
        } else {
            var createdOn = blockWithTransactions.get().getTimestamp();
            var transactions = blockWithTransactions.stream()
                    .map(BlockWithTransactions::getTransactions)
                    .flatMap(List::stream)
                    .map(transaction -> toEntity(blockNumber, createdOn, transaction))
                    .collect(Collectors.toList());
            transactionCommands.addTransactions(blockNumber, transactions);
        }

    }

    private logion.backend.model.transaction.Transaction toEntity(long blockNumber, LocalDateTime createdOn, Transaction transaction) {
        var description = TransactionDescription.builder()
                .from(new Ss58Address(transaction.getFrom()))
                .to(Optional.ofNullable(transaction.getTo()).map(Ss58Address::new))
                .transferValue(transaction.getTransferValue())
                .tip(transaction.getTip())
                .fee(transaction.getFee())
                .reserved(transaction.getReserved())
                .pallet(transaction.getPallet())
                .method(transaction.getMethod())
                .createdOn(createdOn)
                .build();
        return transactionFactory.newTransaction(blockNumber, transaction.getExtrinsicIndex(), description);
    }

    private long lastSyncedBlock() {
        return syncPointRepository.findTransactionSyncPoint();
    }

}
