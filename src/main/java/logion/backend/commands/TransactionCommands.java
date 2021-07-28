package logion.backend.commands;

import java.util.List;
import javax.transaction.Transactional;
import logion.backend.annotation.Commands;
import logion.backend.model.sync.SyncPointRepository;
import logion.backend.model.transaction.Transaction;
import logion.backend.model.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Commands
@Transactional
@Component
public class TransactionCommands {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SyncPointRepository syncPointRepository;

    public void addTransactions(long blockNumber, List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
        syncPointRepository.saveTransactionSyncPoint(blockNumber);
    }

    public void updateLastProcessedBlock(long blockNumber) {
        syncPointRepository.saveTransactionSyncPoint(blockNumber);
    }
}
