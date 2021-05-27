package logion.backend.model;

import logion.backend.subkey.SubkeyWrapper;
import logion.backend.subkey.SubkeyWrapper.ExpectingAddress;
import logion.backend.subkey.SubkeyWrapper.ExpectingAddress.ExpectingMessage;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SignatureTest {

    private final Signature signature = new Signature();

    @ParameterizedTest
    @MethodSource
    void createHash(String expectedMessage, Object... attributes) {
        var message = signature.createHash(attributes);
        assertEquals(expectedMessage, message);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createHash() {
        return Stream.of(
                Arguments.of("iNQmb9TmM40TuEX88olXnSCciXgjuSF9o+Fhk28DFYk=", new Object[]{"abcd"}),
                Arguments.of("d6wxm/4ZeeLXmdnmmH5l/rVPYVEcA1UuuumQgmwghZA=", new Object[]{1.2f}),
                Arguments.of("s6jg4fmrG/46NvIx9nb3i7MKUZ0rIebFMMDu6Ou0pdA=", new Object[]{456}),
                Arguments.of("L1IAt8dg2CXiUjCoVZ3wf4uIJWocNgsmhmswXmH0oAU=", new Object[]{"ABC", 123, true})
        );
    }

    @ParameterizedTest
    @MethodSource
    void testVerify(boolean success) {
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
        Executable executable = () -> signature.verify("signature")
                .withSs58Address(DefaultAddresses.BOB)
                .withMessageBuiltFrom("abcd");
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
                Arguments.of(true),
                Arguments.of(false)
        );
    }
}
