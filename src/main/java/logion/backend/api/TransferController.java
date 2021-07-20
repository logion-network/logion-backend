package logion.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDateTime;
import java.util.List;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.FetchTransfersResponseView;
import logion.backend.api.view.FetchTransfersSpecificationView;
import logion.backend.api.view.TransferView;
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
        return FetchTransfersResponseView.builder()
                .transfers(
                        List.of(
                                toView(specificationView.getAddress(), "5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY"),
                                toView("5CSbpCKSTvZefZYddesUQ9w6NDye2PHbf12MwBZGBgzGeGoo", specificationView.getAddress())))
                .build();

    }

    private TransferView toView(String from, String to) {
        return TransferView.builder()
                .from(from)
                .to(to)
                .createdOn(LocalDateTime.now())
                .value(Long.toString(45678))
                .build();
    }
}
