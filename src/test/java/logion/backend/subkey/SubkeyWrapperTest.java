package logion.backend.subkey;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("IntegrationTest")
public class SubkeyWrapperTest {

    @Test
    public void verifiesRightSignature() {
        givenAddress("5Gv8YYFu8H1btvmrJy9FjjAWfb99wrhV3uhPFoNEr918utyR");
        givenMessage("test message\n");
        givenSignature("22f91b41ba12f8663ddce26bfc90dbfe6a51683fd3782ad679ab2a5fdbe7d44c2a119f22c74eea22555e5483eb7f42b828f189a38379d59c3b607d2461f0858e");
        whenVerifying();
        thenVerificationResultIs(true);
    }

    private void givenAddress(String address) {
        this.address = address;
    }

    private String address;

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

    @Test
    public void rejectsWrongSignature() {
        givenAddress("5Gv8YYFu8H1btvmrJy9FjjAWfb99wrhV3uhPFoNEr918utyR");
        givenMessage("another message\n");
        givenSignature("22f91b41ba12f8663ddce26bfc90dbfe6a51683fd3782ad679ab2a5fdbe7d44c2a119f22c74eea22555e5483eb7f42b828f189a38379d59c3b607d2461f0858e");
        whenVerifying();
        thenVerificationResultIs(false);
    }
}
