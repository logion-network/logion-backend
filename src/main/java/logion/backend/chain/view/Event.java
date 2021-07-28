package logion.backend.chain.view;

import com.fasterxml.jackson.databind.JsonNode;
import logion.backend.annotation.View;
import lombok.Data;

@View
@Data
public class Event {

    Method method;
    JsonNode[] data;
}
