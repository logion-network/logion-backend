package logion.backend.model.protectionrequest;

import java.util.List;
import java.util.UUID;
import logion.backend.annotation.Repository;
import logion.backend.model.Ss58Address;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface ProtectionRequestRepository
        extends CrudRepository<ProtectionRequestAggregateRoot, UUID>,
        QuerydslPredicateExecutor<ProtectionRequestAggregateRoot> {

    List<ProtectionRequestAggregateRoot> findByRequesterAddress(Ss58Address requesterAddress);

}
