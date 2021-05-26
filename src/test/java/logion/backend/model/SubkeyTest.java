package logion.backend.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubkeyTest {

    private final Subkey subkey = new Subkey();

    @ParameterizedTest
    @MethodSource
    void createMessage(String expectedMessage, Object... attributes) {
        var message = subkey.createHash(attributes);
        assertEquals(expectedMessage, message);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createMessage() {
        return Stream.of(
                Arguments.of("iNQmb9TmM40TuEX88olXnSCciXgjuSF9o+Fhk28DFYk=", new Object[]{"abcd"}),
                Arguments.of("d6wxm/4ZeeLXmdnmmH5l/rVPYVEcA1UuuumQgmwghZA=", new Object[]{1.2f}),
                Arguments.of("s6jg4fmrG/46NvIx9nb3i7MKUZ0rIebFMMDu6Ou0pdA=", new Object[]{456}),
                Arguments.of("L1IAt8dg2CXiUjCoVZ3wf4uIJWocNgsmhmswXmH0oAU=", new Object[]{"ABC", 123, true})
                );
    }
}
