package logion.backend.model;

import java.io.Serializable;
import logion.backend.annotation.ValueObject;
import lombok.Value;

/**
 * Wraps the string representation of an
 * <a href="https://github.com/paritytech/substrate/wiki/External-Address-Format-(SS58)">SS58 address</a>.
 */
@ValueObject
@Value
public class Ss58Address implements Serializable, Comparable<Ss58Address> {

    String rawValue;

    @Override
    public int compareTo(Ss58Address address) {
        return this.rawValue.compareTo(address.rawValue);
    }
}
