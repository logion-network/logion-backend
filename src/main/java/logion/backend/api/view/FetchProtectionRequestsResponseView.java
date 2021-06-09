package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import logion.backend.annotation.View;
import lombok.Builder;
import lombok.Value;

@View
@Value
@Builder
@ApiModel(description = "The fetched Protection Requests")
public class FetchProtectionRequestsResponseView {

    @ApiModelProperty("The Protection Requests matching provided specification")
    List<ProtectionRequestView> requests;

}
