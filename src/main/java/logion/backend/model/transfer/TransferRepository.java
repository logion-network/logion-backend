package logion.backend.model.transfer;

import com.querydsl.core.BooleanBuilder;
import java.util.ArrayList;
import java.util.List;
import logion.backend.model.Ss58Address;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import static logion.backend.model.transfer.QTransfer.transfer;

public interface TransferRepository
        extends CrudRepository<Transfer, Transfer.TransferId>, QuerydslPredicateExecutor<Transfer> {

    default List<Transfer> findByAddress(Ss58Address address) {
        var predicate = new BooleanBuilder()
                .or(transfer.from.eq(address))
                .or(transfer.to.eq(address));
        var results = new ArrayList<Transfer>();
        findAll(predicate, transfer.id.blockId.desc(), transfer.id.extrinsicIndex.desc())
                .forEach(results::add);
        return results;
    }
}
