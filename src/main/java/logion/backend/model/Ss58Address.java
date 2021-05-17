package logion.backend.model;

import logion.backend.annotation.ValueObject;
import lombok.Value;

/**
 * Wraps the string representation of an
 * <a href="https://github.com/paritytech/substrate/wiki/External-Address-Format-(SS58)">SS58 address</a>.
 */
@Value
@ValueObject
public class Ss58Address {

    private String rawValue;
}
