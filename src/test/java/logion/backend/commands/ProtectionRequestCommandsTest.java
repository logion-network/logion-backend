package logion.backend.commands;

import java.util.UUID;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProtectionRequestCommandsTest {

    @Test
    void addProtectionRequest() {
        givenProtectionRequest();
        givenProtectionRequestExists(false);
        whenAddProtectionRequest();
        thenRequestSaved();
    }

    private void givenProtectionRequest() {
        var requestId = UUID.randomUUID();
        request = mock(ProtectionRequestAggregateRoot.class);
        when(request.getId()).thenReturn(requestId);
    }

    private ProtectionRequestAggregateRoot request;

    private void givenProtectionRequestExists(boolean exists) {
        when(protectionRequestRepository.existsById(request.getId())).thenReturn(exists);
    }

    @Mock
    private ProtectionRequestRepository protectionRequestRepository;

    private void whenAddProtectionRequest() {
        commands.addProtectionRequest(request);
    }

    @InjectMocks
    private ProtectionRequestCommands commands;

    private void thenRequestSaved() {
        verify(protectionRequestRepository).save(request);
    }

    @Test
    void addProtectionRequestFailsIfExisting() {
        givenProtectionRequest();
        givenProtectionRequestExists(true);
        assertThrows(IllegalArgumentException.class, this::whenAddProtectionRequest);
    }
}
