package logion.backend.subkey;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SubkeyWrapper {

    public ExpectingAddress verify(String signature) {
        requireNonNull(signature);
        var verifier = new ExpectingAddress();
        verifier.signature = signature;
        return verifier;
    }

    public class ExpectingAddress {

        private String signature;

        public ExpectingMessage withSs58Address(String address) {
            var expectsMessage = new ExpectingMessage();
            expectsMessage.address = address;
            return expectsMessage;
        }

        public class ExpectingMessage {

            private String address;

            public boolean withMessage(String message) {
                var command = buildSubkeyCommand();
                command.add("verify");
                command.add(signature);
                command.add(address);
                return callSubkeyWithInput(message, command).exitValue() == 0;
            }

            private List<String> buildSubkeyCommand() {
                var command = new ArrayList<String>();
                command.add(subkey);
                return command;
            }

            private Process callSubkeyWithInput(String input, List<String> command) {
                try {
                    var process = new ProcessBuilder(command.toArray(new String[command.size()])).start();
                    var writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8);
                    writer.write(input);
                    writer.close();
                    process.waitFor();
                    return process;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SubkeyException("Failed to execute subkey", e);
                } catch (Exception e) {
                    throw new SubkeyException("Failed to execute subkey", e);
                }
            }

            private ExpectingMessage() {

            }
        }

        private ExpectingAddress() {

        }
    }

    private String subkey;

    public static SubkeyWrapper defaultInstance() {
        return new SubkeyWrapper.Builder().build();
    }

    public static class Builder {

        public SubkeyWrapper build() {
            return wrapper;
        }

        private SubkeyWrapper wrapper = new SubkeyWrapper();

        public Builder withSubkey(String subkey) {
            wrapper.subkey = subkey;
            return this;
        }
    }

    private SubkeyWrapper() {
        subkey = "subkey";
    }
}
