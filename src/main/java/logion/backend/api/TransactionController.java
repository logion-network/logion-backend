package logion.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.stream.Collectors;
import logion.backend.annotation.RestQuery;
import logion.backend.api.view.FetchTransactionsResponseView;
import logion.backend.api.view.FetchTransactionsSpecificationView;
import logion.backend.api.view.TransactionView;
import logion.backend.model.Ss58Address;
import logion.backend.model.transaction.Transaction;
import logion.backend.model.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/transaction", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Api(tags = "Transactions", value = "TransactionController", description = "Handling of Transactions")
public class TransactionController {

    @PutMapping
    @RestQuery
    @ApiOperation(
            value = "Lists Transactions based on a given specification",
            notes = "No authentication required yet"
    )
    public FetchTransactionsResponseView fetchTransactions(
            @RequestBody
            @ApiParam(value = "The specifications for the expected transactions", name="body")
                    FetchTransactionsSpecificationView specificationView) {
        var transactions = repository.findByAddress(new Ss58Address(specificationView.getAddress()))
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
        return FetchTransactionsResponseView.builder()
                .transactions(transactions)
                .build();
    }

    private TransactionView toView(Transaction transaction) {
        var description = transaction.getDescription();
        var total =
                description.getTransferValue() +
                description.getTip() +
                description.getFee() +
                description.getReserved();
        return TransactionView.builder()
                .from(description.getFrom().getRawValue())
                .to(description.getTo().getRawValue())
                .createdOn(description.getCreatedOn())
                .pallet(description.getPallet())
                .method(description.getMethod())
                .transferValue(Long.toString(description.getTransferValue()))
                .tip(Long.toString(description.getTip()))
                .fee(Long.toString(description.getFee()))
                .reserved(Long.toString(description.getReserved()))
                .total(Long.toString(total))
                .build();
    }

    @Autowired
    private TransactionRepository repository;
}
