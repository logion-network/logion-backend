package logion.backend.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static java.util.Arrays.asList;

public class Hashing {

    public static Hash sha256(Object... attributes) {
        return hash("SHA-256", attributes);
    }

    public static Hash hash(String algorithm, Object... attributes) {
        return hash(algorithm, asList(attributes));
    }

    public static Hash sha256(List<Object> attributes) {
        return hash("SHA-256", attributes);
    }

    public static Hash hash(String algorithm, List<Object> attributes) {
        try {
            var digest = MessageDigest.getInstance(algorithm);
            attributes.stream()
                    .map(Object::toString)
                    .map(s -> s.getBytes(StandardCharsets.UTF_8))
                    .forEach(digest::update);
            return Hash.ofBytes(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
