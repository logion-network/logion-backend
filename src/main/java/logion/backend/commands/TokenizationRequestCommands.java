package logion.backend.commands;

import java.util.UUID;
import javax.transaction.Transactional;
import logion.backend.annotation.Commands;
import logion.backend.model.tokenizationrequest.TokenizationRequestAggregateRoot;
import logion.backend.model.tokenizationrequest.TokenizationRequestRepository;
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

    public void rejectTokenizationRequest(UUID requestId) {
        var request = tokenizationRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request does not exist"));
        request.reject();
        tokenizationRequestRepository.save(request);
    }
}
