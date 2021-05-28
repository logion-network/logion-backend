package logion.backend.commands;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.transaction.Transactional;
import logion.backend.annotation.Commands;
import logion.backend.crypto.Hashing;
import logion.backend.model.tokenizationrequest.AssetDescription;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Commands
@Transactional
@Component
public class TokenizationRequestCommands {

    public TokenizationRequestAggregateRoot addTokenizationRequest(TokenizationRequestAggregateRoot request) {
        if(tokenizationRequestRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("A request with given ID already exists");
        }
        return tokenizationRequestRepository.save(request);
    }

    @Autowired
    private TokenizationRequestRepository tokenizationRequestRepository;

    public void rejectTokenizationRequest(UUID requestId, String rejectReason, LocalDateTime rejectedOn) {
        var request = tokenizationRequestRepository.findById(requestId)
                .orElseThrow(TokenizationRequestRepository.requestNotFound);
        request.reject(rejectReason, rejectedOn);
        tokenizationRequestRepository.save(request);
    }

    public String acceptTokenizationRequest(UUID requestId, LocalDateTime acceptedOn) {
        var request = tokenizationRequestRepository.findById(requestId)
                .orElseThrow(TokenizationRequestRepository.requestNotFound);
        var sessionToken = RandomStringUtils.random(32);
        request.accept(acceptedOn, Hashing.sha256(sessionToken));
        tokenizationRequestRepository.save(request);
        return sessionToken;
    }

    public void setAssetDescription(UUID requestId, String sessionToken, AssetDescription description) {
        var request = tokenizationRequestRepository.findById(requestId)
                .orElseThrow(TokenizationRequestRepository.requestNotFound);
        var sessionTokenHash = Hashing.sha256(sessionToken);
        request.setAssetDescription(sessionTokenHash, description);
        tokenizationRequestRepository.save(request);
    }
}
