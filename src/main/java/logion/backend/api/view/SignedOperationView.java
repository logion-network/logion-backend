package logion.backend.api.view;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SignedOperationView {

    @ApiModelProperty("<p>A base64-encoded SHA256 hash of a concatenation of</p><ol><li>a resource</li><li>an operation</li><li>the <code>signedOn</code> field</li><li>additional fields in function of the request</li></ol>")
    String signature;

    @ApiModelProperty("The signature's timestamp")
    LocalDateTime signedOn;
}
