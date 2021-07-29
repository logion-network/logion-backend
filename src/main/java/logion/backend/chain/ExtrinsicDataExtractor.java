package logion.backend.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import logion.backend.chain.util.SidecarException;
import logion.backend.chain.view.ArgBalancesTransfer;
import logion.backend.chain.view.ArgTimestampSet;
import logion.backend.chain.view.Event;
import logion.backend.chain.view.Extrinsic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static java.util.Arrays.stream;

@Component
public class ExtrinsicDataExtractor {

    @Autowired
    @Qualifier("sidecarObjectMapper")
    private ObjectMapper sidecarObjectMapper;

    public LocalDateTime getTimestamp(Extrinsic extrinsic) {
        ArgTimestampSet argTimestampSet = readArgs(extrinsic, ArgTimestampSet.class);
        return Instant.ofEpochMilli(argTimestampSet.getNow())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public ArgBalancesTransfer getArgBalancesTransfer(Extrinsic extrinsic) {
        return readArgs(extrinsic, ArgBalancesTransfer.class);
    }

    public String[] getData(Event event) {
        return stream(event.getData())
                .map(JsonNode::asText)
                .toArray(String[]::new);
    }

    private <T> T readArgs(Extrinsic extrinsic, Class<T> clazz) {
        try {
            return sidecarObjectMapper.reader()
                    .readValue(extrinsic.getArgs(), clazz);
        } catch (Exception e) {
            throw new SidecarException(e);
        }
    }
}
