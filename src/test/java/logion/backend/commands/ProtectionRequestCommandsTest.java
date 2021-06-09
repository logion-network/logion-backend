package logion.backend.commands;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
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

    private static final LocalDateTime REJECTED_ON = LocalDateTime.now();
    private static final String REJECT_REASON = "Illegal";
    private static final LocalDateTime ACCEPTED_ON = REJECTED_ON.plusMinutes(1);

    @Test
    void addProtectionRequest() {
        givenProtectionRequest();
        givenProtectionRequestExists(false);
        whenAddProtectionRequest();
        thenRequestSaved();
    }

    @Test
    void acceptTokenizationRequest() {
        givenProtectionRequest();
        givenProtectionRequestFindable();
        whenAcceptProtectionRequest(DefaultAddresses.ALICE);
        thenRequestAccepted(DefaultAddresses.ALICE);
        thenRequestSaved();
    }

    @Test
    void rejectTokenizationRequest() {
        givenProtectionRequest();
        givenProtectionRequestFindable();
        whenRejectProtectionRequest(DefaultAddresses.ALICE);
        thenRequestRejected(DefaultAddresses.ALICE);
        thenRequestSaved();
    }

    private void givenProtectionRequestFindable() {
        when(protectionRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
    }

    private void whenAcceptProtectionRequest(Ss58Address legalOfficerAddress) {
        commands.acceptProtectionRequest(request.getId(), legalOfficerAddress, ACCEPTED_ON);
    }

    private void whenRejectProtectionRequest(Ss58Address legalOfficerAddress) {
        commands.rejectProtectionRequest(request.getId(), legalOfficerAddress, REJECT_REASON, REJECTED_ON);
    }

    private void thenRequestAccepted(Ss58Address legalOfficerAddress) {
        verify(request).accept(legalOfficerAddress, ACCEPTED_ON);
    }

    private void thenRequestRejected(Ss58Address legalOfficerAddress) {
        verify(request).reject(legalOfficerAddress, REJECT_REASON, REJECTED_ON);
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
