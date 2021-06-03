package logion.backend.api.view;

import io.swagger.annotations.ApiModel;
import logion.backend.annotation.View;
import lombok.Data;
import lombok.EqualsAndHashCode;

@View
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "The Tokenization Request to accept")
public class AcceptTokenRequestView extends SignedOperationView {
}
