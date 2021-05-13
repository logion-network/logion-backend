package logion.backend.model.tokenizationrequest;

import java.util.List;
import java.util.UUID;
import logion.backend.annotation.Repository;
import logion.backend.model.Ss58Address;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface TokenizationRequestRepository extends CrudRepository<TokenizationRequestAggregateRoot, UUID> {

    List<TokenizationRequestAggregateRoot> findByLegalOfficerAddress(Ss58Address address);
}
