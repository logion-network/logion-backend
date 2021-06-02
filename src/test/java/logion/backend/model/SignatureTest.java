package logion.backend.model;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignatureTest {

    @ParameterizedTest
    @MethodSource
    void testVerify(boolean success, String expectedMessage, Object... attributes) {
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
                    .withTimestamp(LocalDateTime.of(2021, Month.MAY, 10, 0, 0));

            if (attributes == null) {
                em.withoutMessage();
            } else {
                em.withMessageBuiltFrom(attributes);
            }
        };
        // Then
        if (success) {
            assertDoesNotThrow(executable);
            verify(expectingMessage).withMessage(expectedMessage);
        } else {
            assertThrows(ResponseStatusException.class, executable);
        }
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> testVerify() {
        var sameSignature = "FtvKwzH/OdYXynVMDeOh6WD77O5gYD8LtDzs5qqDf2U=";
        return Stream.of(
                Arguments.of(true, "SdPF9uK+K2RNcs0m0OYPXTTNUhJ06/+v8CcZrv9f8jo=", new Object[] {"abcd"}),
                Arguments.of(false, null, new Object[] {"abcd"}),
                Arguments.of(true, "CjwOkiDFvZWqt+uZYPktkdggygroB60g0mVn7QxyZm8=", null),
                Arguments.of(false, null, null),
                Arguments.of(true, sameSignature, new Object[]{"abc", 123, true}),
                Arguments.of(true, sameSignature, new Object[]{List.of("abc", 123, true)}),
                Arguments.of(true, sameSignature, new Object[]{"abc", List.of(123, true)}),
                Arguments.of(true, sameSignature, new Object[]{"abc", new Object[]{123, true}})
        );
    }
}
