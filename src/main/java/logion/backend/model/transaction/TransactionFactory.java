package logion.backend.model.transaction;

import logion.backend.annotation.Factory;
import logion.backend.model.transaction.Transaction.TransactionId;

@Factory
public class TransactionFactory {

    public Transaction newTransaction(long blockNumber, int extrinsicIndex, TransactionDescription description) {
        var transaction = new Transaction();
        transaction.id = new TransactionId(blockNumber, extrinsicIndex);
        transaction.from = description.getFrom();
        transaction.to = description.getTo();
        transaction.transferValue = description.getTransferValue();
        transaction.tip = description.getTip();
        transaction.fee = description.getFee();
        transaction.reserved = description.getReserved();
        transaction.pallet = description.getPallet();
        transaction.method = description.getMethod();
        transaction.createdOn = description.getCreatedOn();
        return transaction;
    }
}
