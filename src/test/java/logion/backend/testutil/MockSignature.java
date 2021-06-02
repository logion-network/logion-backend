package logion.backend.testutil;

import java.time.LocalDateTime;
import logion.backend.model.Signature;
import logion.backend.model.Signature.ExpectingAddress;
import logion.backend.model.Ss58Address;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockSignature {

    public static ExpectingAddress signatureVerifyMock(Ss58Address address, String resource, String operation, boolean verifyResult, Object... attributes) {
        var expectingMessage = mock(ExpectingAddress.ExpectingMessage.class);
        when(expectingMessage.withResource(resource)).thenReturn(expectingMessage);
        when(expectingMessage.withOperation(operation)).thenReturn(expectingMessage);
        when(expectingMessage.withTimestamp(any(LocalDateTime.class))).thenReturn(expectingMessage);
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify signature"))
                .when(expectingMessage).withMessageBuiltFrom(any());
        if (verifyResult) {
            doNothing().when(expectingMessage).withMessageBuiltFrom(attributes);
        } else {
            doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify signature"))
                    .when(expectingMessage).withMessageBuiltFrom(attributes);
        }

        var expectingAddress = mock(ExpectingAddress.class);
        when(expectingAddress.withSs58Address(address)).thenReturn(expectingMessage);

        return expectingAddress;
    }
}
