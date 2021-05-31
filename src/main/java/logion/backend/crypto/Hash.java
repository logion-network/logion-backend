package logion.backend.crypto;

import java.util.Base64;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Hash {

    public static Hash ofBytes(byte[] bytes) {
        return new Hash(bytes);
    }

    private Hash(byte[] bytes) {
        this.bytes = bytes;
    }

    private final byte[] bytes;

    public String toBase64() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static Hash ofBase64(String base64) {
        var bytes = Base64.getDecoder().decode(base64);
        return new Hash(bytes);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(bytes)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        Hash other = (Hash) obj;
        return new EqualsBuilder()
                .append(bytes, other.bytes)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("base64", toBase64())
                .build();
    }
}
