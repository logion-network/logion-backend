package logion.backend.chain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtil {

    public static String readResource(String responseFile) {
        try {
            var resource = TestUtil.class.getClassLoader().getResource("sidecar/" + responseFile);
            assert resource != null;
            return Files.readString(Paths.get(resource.getPath()));
        } catch (IOException ioe) {
            throw new RuntimeException("Failed to read file " + responseFile, ioe);
        }

    }
}
