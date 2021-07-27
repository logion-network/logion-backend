package logion.backend.model.transaction;

import com.querydsl.core.BooleanBuilder;
import java.util.ArrayList;
import java.util.List;
import logion.backend.model.Ss58Address;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import static logion.backend.model.transaction.QTransaction.transaction;

public interface TransactionRepository
        extends CrudRepository<Transaction, Transaction.TransactionId>, QuerydslPredicateExecutor<Transaction> {

    default List<Transaction> findByAddress(Ss58Address address) {
        var predicate = new BooleanBuilder()
                .or(transaction.from.eq(address))
                .or(transaction.to.eq(address));
        var results = new ArrayList<Transaction>();
        findAll(predicate, transaction.id.blockId.desc(), transaction.id.extrinsicIndex.desc())
                .forEach(results::add);
        return results;
    }
}
