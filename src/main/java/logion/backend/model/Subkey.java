package logion.backend.model;

import logion.backend.subkey.SubkeyWrapper;
import org.springframework.stereotype.Service;

@Service
public class Subkey {

    public SubkeyWrapper.ExpectingAddress verify(String signature) {
        return SubkeyWrapper.defaultInstance().verify(signature);
    }
}
