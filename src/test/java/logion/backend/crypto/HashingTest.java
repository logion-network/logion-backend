package logion.backend.crypto;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashingTest {

    @ParameterizedTest
    @MethodSource
    void sha256Hash(String expectedMessage, Object... attributes) {
        var message = Hashing.sha256(attributes);
        assertEquals(Hash.ofBase64(expectedMessage), message);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> sha256Hash() {
        return Stream.of(
                Arguments.of("iNQmb9TmM40TuEX88olXnSCciXgjuSF9o+Fhk28DFYk=", new Object[]{"abcd"}),
                Arguments.of("d6wxm/4ZeeLXmdnmmH5l/rVPYVEcA1UuuumQgmwghZA=", new Object[]{1.2f}),
                Arguments.of("s6jg4fmrG/46NvIx9nb3i7MKUZ0rIebFMMDu6Ou0pdA=", new Object[]{456}),
                Arguments.of("L1IAt8dg2CXiUjCoVZ3wf4uIJWocNgsmhmswXmH0oAU=", new Object[]{"ABC", 123, true})
        );
    }
}
