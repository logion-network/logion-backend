package logion.backend.model;

import logion.backend.subkey.SubkeyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Subkey implements InitializingBean {

    public SubkeyWrapper.ExpectingAddress verify(String signature) {
        return newWrapper().verify(signature);
    }

    private SubkeyWrapper newWrapper() {
        return new SubkeyWrapper.Builder()
                .withSubkey(subkeyPath)
                .build();
    }

    @Value("${logion.subkey.command:subkey}")
    private String subkeyPath;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Subkey command: {}", subkeyPath);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
}
