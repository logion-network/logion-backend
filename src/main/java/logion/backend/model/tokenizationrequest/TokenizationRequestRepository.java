package logion.backend.model.tokenizationrequest;

import com.querydsl.core.BooleanBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import logion.backend.annotation.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface TokenizationRequestRepository
extends CrudRepository<TokenizationRequestAggregateRoot, UUID>,
QuerydslPredicateExecutor<TokenizationRequestAggregateRoot> {

    default List<TokenizationRequestAggregateRoot> findBy(FetchRequestsSpecification query) {
        var root = QTokenizationRequestAggregateRoot.tokenizationRequestAggregateRoot;
        var predicate = new BooleanBuilder().and(root.status.eq(query.getExpectedStatus()));
        query.getExpectedLegalOfficer().ifPresent(legalOfficer -> predicate.and(root.legalOfficerAddress.eq(legalOfficer)));
        query.getExpectedRequesterAddress().ifPresent(requesterAddress -> predicate.and(root.requesterAddress.eq(requesterAddress)));
        var results = new ArrayList<TokenizationRequestAggregateRoot>();
        findAll(predicate).forEach(results::add);
        return results;
    }
}
