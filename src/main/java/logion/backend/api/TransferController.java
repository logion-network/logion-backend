package logion.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.stream.Collectors;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.FetchTransfersResponseView;
import logion.backend.api.view.FetchTransfersSpecificationView;
import logion.backend.api.view.TransferView;
import logion.backend.model.Ss58Address;
import logion.backend.model.transfer.Transfer;
import logion.backend.model.transfer.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/transfer", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Api(tags = "Transfers", value = "TransferController", description = "Handling of Transfers")
public class TransferController {

    @PutMapping
    @RestQuery
    @ApiOperation(
            value = "Lists Transfers based on a given specification",
            notes = "No authentication required yet"
    )
    public FetchTransfersResponseView fetchTransfers(
            @RequestBody
            @ApiParam(value = "The specifications for the expected transfers", name="body")
                    FetchTransfersSpecificationView specificationView) {
        var transfers = repository.findByAddress(new Ss58Address(specificationView.getAddress()))
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
        return FetchTransfersResponseView.builder()
                .transfers(transfers)
                .build();
    }

    private TransferView toView(Transfer transfer) {
        var description = transfer.getDescription();
        return TransferView.builder()
                .from(description.getFrom().getRawValue())
                .to(description.getTo().getRawValue())
                .createdOn(description.getCreatedOn())
                .value(Long.toString(description.getValue()))
                .build();
    }

    @Autowired
    private TransferRepository repository;
}
