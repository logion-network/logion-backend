package logion.backend.subkey;

@SuppressWarnings("serial")
public class SubkeyException extends RuntimeException {

    public SubkeyException() {

    }

    public SubkeyException(String message) {
        super(message);
    }

    public SubkeyException(Throwable cause) {
        super(cause);
    }

    public SubkeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
