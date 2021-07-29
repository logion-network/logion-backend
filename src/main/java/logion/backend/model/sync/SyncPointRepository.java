package logion.backend.model.sync;

import logion.backend.annotation.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface SyncPointRepository extends CrudRepository<SyncPoint, String> {

    String NAME_TRANSACTION = "Transaction";

    default long findTransactionSyncPoint() {
        return findById(NAME_TRANSACTION)
                .map(SyncPoint::getLatestHeadBlockNumber)
                .orElse(0L);
    }

    default void saveTransactionSyncPoint(long blockNumber) {
        save(new SyncPoint(NAME_TRANSACTION, blockNumber));
    }
}
