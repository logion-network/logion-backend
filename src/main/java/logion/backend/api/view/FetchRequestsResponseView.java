package logion.backend.api.view;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FetchRequestsResponseView {

    List<TokenRequestView> requests;
}
