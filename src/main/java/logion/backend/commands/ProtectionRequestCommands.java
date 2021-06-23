package logion.backend.commands;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.transaction.Transactional;
import logion.backend.annotation.Commands;
import logion.backend.chain.RecoveryService;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import logion.backend.model.protectionrequest.ProtectionRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Commands
@Transactional
@Component
public class ProtectionRequestCommands {

    @Autowired
    private RecoveryService recoveryService;

    public ProtectionRequestAggregateRoot addProtectionRequest(ProtectionRequestAggregateRoot request) {
        if(repository.existsById(request.getId())) {
            throw new IllegalArgumentException("A request with given ID already exists");
        }
        return repository.save(request);
    }

    public void rejectProtectionRequest(UUID requestId, Ss58Address legalOfficerAddress, String reason, LocalDateTime rejectedOn) {
        var request = repository.findById(requestId)
                .orElseThrow(ProtectionRequestRepository.requestNotFound);
        request.reject(legalOfficerAddress, reason, rejectedOn);
        repository.save(request);
    }

    public void acceptProtectionRequest(UUID requestId, Ss58Address legalOfficerAddress, LocalDateTime acceptedOn) {
        var request = repository.findById(requestId)
                .orElseThrow(ProtectionRequestRepository.requestNotFound);
        request.accept(legalOfficerAddress, acceptedOn);
        repository.save(request);
    }

    public ProtectionRequestAggregateRoot checkAndSetProtectionRequestActivation(UUID requestId) {
        var request = repository.findById(requestId)
                .orElseThrow(ProtectionRequestRepository.requestNotFound);
        if (request.getStatus() == ProtectionRequestStatus.ACTIVATED) {
            return request;
        }
        var requesterAddress = request.getDescription().getRequesterAddress();
        var recoveryConfig = recoveryService.getRecoveryConfig(requesterAddress);
        return recoveryConfig
                .map(rc -> {
                    request.setActivated();
                    return repository.save(request);
                })
                .orElse(request);
    }

    @Autowired
    private ProtectionRequestRepository repository;
}
