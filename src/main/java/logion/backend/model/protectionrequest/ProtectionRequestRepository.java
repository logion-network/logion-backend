package logion.backend.model.protectionrequest;

import com.querydsl.core.BooleanBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import logion.backend.annotation.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import static com.querydsl.jpa.JPAExpressions.selectFrom;

@Repository
public interface ProtectionRequestRepository
        extends CrudRepository<ProtectionRequestAggregateRoot, UUID>,
        QuerydslPredicateExecutor<ProtectionRequestAggregateRoot> {

    Supplier<IllegalArgumentException> requestNotFound = () -> new IllegalArgumentException("Protection request does not exist");

    default List<ProtectionRequestAggregateRoot> findBy(FetchProtectionRequestsSpecification query) {

        var root = QProtectionRequestAggregateRoot.protectionRequestAggregateRoot;
        var legalOfficerDecision = QLegalOfficerDecision.legalOfficerDecision;

        var statusCondition = new BooleanBuilder();
        query.getExpectedStatuses()
                .forEach(status -> statusCondition.or(legalOfficerDecision.status.eq(status)));

        var whereClause = new BooleanBuilder(statusCondition);
        query.getExpectedLegalOfficer().ifPresent(legalOfficer -> whereClause.and(legalOfficerDecision.id.legalOfficerAddress.eq(legalOfficer)));

        var subQuery =
                selectFrom(legalOfficerDecision)
                .where(whereClause);

        var predicate = new BooleanBuilder(root.decisions.any().in(subQuery));
        query.getExpectedRequesterAddress().ifPresent(requester -> predicate.and(root.requesterAddress.eq(requester)));

        var results = new ArrayList<ProtectionRequestAggregateRoot>();
        findAll(predicate).forEach(results::add);
        return results;
    }
}
