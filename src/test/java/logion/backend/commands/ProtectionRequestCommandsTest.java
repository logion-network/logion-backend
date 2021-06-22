package logion.backend.commands;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import logion.backend.chain.RecoveryService;
import logion.backend.chain.view.RecoveryConfig;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestDescription;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import logion.backend.model.protectionrequest.ProtectionRequestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    void acceptProtectionRequest() {
        givenProtectionRequest();
        givenProtectionRequestFindable();
        whenAcceptProtectionRequest(DefaultAddresses.ALICE);
        thenRequestAccepted(DefaultAddresses.ALICE);
        thenRequestSaved();
    }

    @Test
    void rejectProtectionRequest() {
        givenProtectionRequest();
        givenProtectionRequestFindable();
        whenRejectProtectionRequest(DefaultAddresses.ALICE);
        thenRequestRejected(DefaultAddresses.ALICE);
        thenRequestSaved();
    }

    @Test
    void checkAndSetProtectionRequestActivated() {
        givenProtectionRequest(ProtectionRequestStatus.PENDING);
        givenProtectionRequestFindable();
        givenRecoveryConfigExists(true);
        whenCheckAndSetProtectionRequestActivated();
        thenRequestActivated();
        thenRequestSaved();
    }

    @Test
    void checkAndSetProtectionRequestNotActivated() {
        givenProtectionRequest(ProtectionRequestStatus.PENDING);
        givenProtectionRequestFindable();
        givenRecoveryConfigExists(false);
        whenCheckAndSetProtectionRequestActivated();
        thenRequestNotActivated();
        thenRequestNotSaved();
    }

    @Test
    void checkAndSetProtectionRequestAlreadyActivated() {
        givenProtectionRequest(ProtectionRequestStatus.ACTIVATED);
        givenProtectionRequestFindable();
        whenCheckAndSetProtectionRequestActivated();
        thenRequestNotActivated();
        thenRequestNotSaved();
    }

    private void givenProtectionRequestFindable() {
        when(protectionRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
    }

    private void whenCheckAndSetProtectionRequestActivated() {
        commands.checkAndSetProtectionRequestActivation(request.getId());
    }

    private void thenRequestActivated() {
        verify(request).setActivated();
    }

    private void thenRequestNotActivated() {
        verify(request, never()).setActivated();
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
        givenProtectionRequest(null);
    }

    private void givenProtectionRequest(ProtectionRequestStatus status) {
        var requestId = UUID.randomUUID();
        request = mock(ProtectionRequestAggregateRoot.class);
        when(request.getId()).thenReturn(requestId);
        if (status != null) { // to avoid UnnecessaryStubbingException
            when(request.getStatus()).thenReturn(status);
            description = ProtectionRequestDescription.builder()
                    .requesterAddress(new Ss58Address("some-address"))
                    .build();
            if (status == ProtectionRequestStatus.PENDING) { // to avoid UnnecessaryStubbingException
                when(request.getDescription()).thenReturn(description);
            }
        }
    }

    private ProtectionRequestAggregateRoot request;
    private ProtectionRequestDescription description;

    private void givenProtectionRequestExists(boolean exists) {
        when(protectionRequestRepository.existsById(request.getId())).thenReturn(exists);
    }

    @Mock
    private ProtectionRequestRepository protectionRequestRepository;

    private void givenRecoveryConfigExists(boolean exists) {
        var recoveryConfig = exists ?
                Optional.of(new RecoveryConfig(0, 0, null, 0)) :
                Optional.<RecoveryConfig>empty();
        when(recoveryService.getRecoveryConfig(description.getRequesterAddress()))
                .thenReturn(recoveryConfig);
    }

    @Mock
    private RecoveryService recoveryService;

    private void whenAddProtectionRequest() {
        commands.addProtectionRequest(request);
    }

    @InjectMocks
    private ProtectionRequestCommands commands;

    private void thenRequestSaved() {
        verify(protectionRequestRepository).save(request);
    }

    private void thenRequestNotSaved() {
        verify(protectionRequestRepository, never()).save(request);
    }

    @Test
    void addProtectionRequestFailsIfExisting() {
        givenProtectionRequest();
        givenProtectionRequestExists(true);
        assertThrows(IllegalArgumentException.class, this::whenAddProtectionRequest);
    }
}
