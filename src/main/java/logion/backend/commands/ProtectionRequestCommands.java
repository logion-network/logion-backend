package logion.backend.commands;

import javax.transaction.Transactional;
import logion.backend.annotation.Commands;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Commands
@Transactional
@Component
public class ProtectionRequestCommands {

    public ProtectionRequestAggregateRoot addProtectionRequest(ProtectionRequestAggregateRoot request) {
        if(repository.existsById(request.getId())) {
            throw new IllegalArgumentException("A request with given ID already exists");
        }
        return repository.save(request);
    }

    @Autowired
    private ProtectionRequestRepository repository;
}
