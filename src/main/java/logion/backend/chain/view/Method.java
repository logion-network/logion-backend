package logion.backend.chain.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import logion.backend.annotation.View;
import lombok.AllArgsConstructor;
import lombok.Data;

@View
@Data
@AllArgsConstructor
public class Method {

    String pallet;

    @JsonProperty("method")
    String name;

}
