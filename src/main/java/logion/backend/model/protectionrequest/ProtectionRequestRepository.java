package logion.backend.model.protectionrequest;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import logion.backend.annotation.Repository;
import logion.backend.model.Ss58Address;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface ProtectionRequestRepository
        extends CrudRepository<ProtectionRequestAggregateRoot, UUID>,
        QuerydslPredicateExecutor<ProtectionRequestAggregateRoot> {

    Supplier<IllegalArgumentException> requestNotFound = () -> new IllegalArgumentException("Protection request does not exist");

    Optional<ProtectionRequestAggregateRoot> findByRequesterAddress(Ss58Address requesterAddress);

}
