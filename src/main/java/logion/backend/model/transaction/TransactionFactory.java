package logion.backend.model.transaction;

import logion.backend.annotation.Factory;
import logion.backend.model.transaction.Transaction.TransactionId;

@Factory
public class TransactionFactory {

    public Transaction newTransaction(long blockNumber, int extrinsicIndex, TransactionDescription description) {
        var transaction = new Transaction();
        transaction.id = new TransactionId(blockNumber, extrinsicIndex);
        transaction.setDescription(description);
        return transaction;
    }
}
