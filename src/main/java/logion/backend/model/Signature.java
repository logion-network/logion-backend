package logion.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import logion.backend.crypto.Hashing;
import logion.backend.subkey.SubkeyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.requireNonNull;

@Service
public class Signature implements InitializingBean {

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

            private String resource;
            private String operation;
            private LocalDateTime signedOn;

            public ExpectingMessage withResource(String resource) {
                this.resource = resource;
                return this;
            }

            public ExpectingMessage withOperation(String operation) {
                this.operation = operation;
                return this;
            }

            public ExpectingMessage withTimestamp(LocalDateTime signedOn) {
                this.signedOn = signedOn;
                return this;
            }

            public void withMessageBuiltFrom(Object... attributes) {
                verify(resource, operation, signedOn, attributes);
            }

            public void withoutMessage() {
                verify(resource, operation, signedOn);
            }

            private void verify(String resource, String operation, LocalDateTime signedOn, Object... otherAttributes) {
                var allAttributes = new ArrayList<>();
                allAttributes.add(requireNonNull(resource, "resource is mandatory to check signature"));
                allAttributes.add(requireNonNull(operation, "operation is mandatory to check signature"));
                allAttributes.add(requireNonNull(signedOn, "timestamp is mandatory to check signature"));
                if (otherAttributes != null) {
                    for (Object otherAttribute:otherAttributes) {
                        allAttributes.addAll(expandArray(otherAttribute));
                    }
                }
                var message = Hashing.sha256(allAttributes);
                var signatureValid = subkeyWrapper.verify(signature)
                        .withSs58Address(address)
                        .withMessage(message.toBase64());
                if (!signatureValid) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify signature");
                }
            }

            private Collection<?> expandArray(Object obj) {
                if (obj.getClass().isArray()) {
                    return Arrays.asList((Object[]) obj);
                }
                if (obj instanceof Collection) {
                    return (Collection<?>) obj;
                }
                return Collections.singleton(obj);
            }
        }
    }

    @Value("${logion.subkey.command:subkey}")
    private String subkeyPath;

    @Override
    public void afterPropertiesSet() {
        logger.info("Subkey command: {}", subkeyPath);
        subkeyWrapper = new SubkeyWrapper.Builder()
                .withSubkey(subkeyPath)
                .build();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
