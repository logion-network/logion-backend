package logion.backend.model;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import logion.backend.subkey.SubkeyWrapper;
import logion.backend.subkey.SubkeyWrapper.ExpectingAddress;
import logion.backend.subkey.SubkeyWrapper.ExpectingAddress.ExpectingMessage;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SignatureTest {

    @ParameterizedTest
    @MethodSource
    void testVerify(boolean success, Object... attributes) {
        // Given
        Signature signature = new Signature();
        var subkeyWrapper = mock(SubkeyWrapper.class);
        var expectingAddress = mock(ExpectingAddress.class);
        var expectingMessage = mock(ExpectingMessage.class);
        when(subkeyWrapper.verify("signature")).thenReturn(expectingAddress);
        when(expectingAddress.withSs58Address(DefaultAddresses.BOB)).thenReturn(expectingMessage);
        when(expectingMessage.withMessage(anyString())).thenReturn(success);
        signature.setSubkeyWrapper(subkeyWrapper);

        // When
        Executable executable = () -> {
            var em = signature.verify("signature")
                    .withSs58Address(DefaultAddresses.BOB)
                    .withResource("resource")
                    .withOperation("operation")
                    .withTimestamp(LocalDateTime.now());

            if (attributes == null) {
                em.withoutMessage();
            } else {
                em.withMessageBuiltFrom(attributes);
            }
        };
        // Then
        if (success) {
            assertDoesNotThrow(executable);
        } else {
            assertThrows(ResponseStatusException.class, executable);
        }
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> testVerify() {
        return Stream.of(
                Arguments.of(true, new Object[] {"abcd"}),
                Arguments.of(false, new Object[] {"abcd"}),
                Arguments.of(true, null),
                Arguments.of(false, null)
        );
    }
}
