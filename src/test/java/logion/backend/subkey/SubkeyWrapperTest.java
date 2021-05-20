package logion.backend.subkey;

import java.util.stream.Stream;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("IntegrationTest")
class SubkeyWrapperTest {

    @ParameterizedTest
    @MethodSource
    void verifySignature(String address, String message, String signature, boolean expectedResult) {
        givenAddress(address);
        givenMessage(message);
        givenSignature(signature);
        whenVerifying();
        thenVerificationResultIs(expectedResult);
    }

    private void givenAddress(String address) {
        this.address = new Ss58Address(address);
    }

    private Ss58Address address;

    private void givenMessage(String message) {
        this.message = message;
    }

    private String message;

    private void givenSignature(String signature) {
        this.signature = signature;
    }

    private String signature;

    private void whenVerifying() {
        var subkey = SubkeyWrapper.defaultInstance();
        verificationResult = subkey
                .verify(signature)
                .withSs58Address(address)
                .withMessage(message);
    }

    private boolean verificationResult;

    private void thenVerificationResultIs(boolean expected) {
        assertThat(verificationResult, is(expected));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> verifySignature() {
        return Stream.of(
            Arguments.of(
                THE_ADDRESS,
                THE_MESSAGE,
                THE_SIGNATURE,
                true
            ),
            Arguments.of(
                THE_ADDRESS,
                ANOTHER_MESSAGE,
                THE_SIGNATURE,
                false
            ),
            Arguments.of(
                THE_ADDRESS,
                THE_MESSAGE,
                HEX_PREFIXED_SIGNATURE,
                true
            )
        );
    }

    private static final String THE_ADDRESS = "5Gv8YYFu8H1btvmrJy9FjjAWfb99wrhV3uhPFoNEr918utyR";

    private static final String THE_MESSAGE = "test message\n";

    private static final String THE_SIGNATURE = "22f91b41ba12f8663ddce26bfc90dbfe6a51683fd3782ad679ab2a5fdbe7d44c2a119f22c74eea22555e5483eb7f42b828f189a38379d59c3b607d2461f0858e";

    private static final String ANOTHER_MESSAGE = "another message\n";

    private static final String HEX_PREFIXED_SIGNATURE = "0x22f91b41ba12f8663ddce26bfc90dbfe6a51683fd3782ad679ab2a5fdbe7d44c2a119f22c74eea22555e5483eb7f42b828f189a38379d59c3b607d2461f0858e";
}
