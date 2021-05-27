package logion.backend.model;

import logion.backend.subkey.SubkeyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.util.Arrays.stream;

@Service
public class Signature implements InitializingBean {

    private static final String ALGORITHM = "SHA-256";

    private SubkeyWrapper subkeyWrapper;

    void setSubkeyWrapper(SubkeyWrapper subkeyWrapper) {
        this.subkeyWrapper = subkeyWrapper;
    }

    public ExpectingAddress verify(String signature) {
        var verifier = new ExpectingAddress();
        verifier.signature = signature;
        return verifier;
    }

    public class ExpectingAddress {

        private String signature;

        public ExpectingMessage withSs58Address(Ss58Address address) {
            var expectsMessage = new ExpectingMessage();
            expectsMessage.address = address;
            return expectsMessage;
        }

        public class ExpectingMessage {

            private Ss58Address address;

            public void withMessageBuiltFrom(Object... attributes) {
                String message = createHash(attributes);
                var signatureValid = subkeyWrapper.verify(signature)
                        .withSs58Address(address)
                        .withMessage(message);
                if (!signatureValid) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify signature");
                }
            }
        }
    }

    String createHash(Object... attributes) {
        try {
            var digest = MessageDigest.getInstance(ALGORITHM);
            stream(attributes)
                    .map(Object::toString)
                    .map(s -> s.getBytes(StandardCharsets.UTF_8))
                    .forEach(digest::update);
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid digest algorithm", e);
        }
    }

    @Value("${logion.subkey.command:subkey}")
    private String subkeyPath;

    @Override
    public void afterPropertiesSet() {
        logger.info("Subkey command: {}", subkeyPath);
        this.subkeyWrapper = new SubkeyWrapper.Builder()
                .withSubkey(subkeyPath)
                .build();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
