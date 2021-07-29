package logion.backend.web;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import logion.backend.api.TransactionController;
import logion.backend.model.Ss58Address;
import logion.backend.model.transaction.Transaction;
import logion.backend.model.transaction.TransactionDescription;
import logion.backend.model.transaction.TransactionRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
class TransactionWebTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.parse("2021-07-20T17:02:22.994321");

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionRepository repository;

    @Test
    void fetchTransactions() throws Exception {

        Ss58Address address1 = new Ss58Address("abcd1234");
        Ss58Address address2 = new Ss58Address("efgh5678");

        var transaction0 = transaction(address1, address2, "balances", "transfer", 13245000000L, 52L, 125000149, 0);
        var transaction1 = transaction(address2, address1, "assets", "setMetadata", 0, 0, 125000141, 23);
        var transactions = List.of(
                transaction0,
                transaction1
        );
        var total0 = transaction0.getDescription().getTransferValue()
                .add(transaction0.getDescription().getFee())
                .add(transaction0.getDescription().getTip());
        var total1 = transaction1.getDescription().getFee()
                .add(transaction1.getDescription().getReserved());

        when(repository.findByAddress(address1))
                .thenReturn(transactions);

        var request = new JSONObject()
                .put("address", address1.getRawValue());

        mvc.perform(put("/transaction")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].from").value(is(address1.getRawValue())))
                .andExpect(jsonPath("$.transactions[0].to").value(is(address2.getRawValue())))
                .andExpect(jsonPath("$.transactions[0].pallet").value(is(transaction0.getDescription().getPallet())))
                .andExpect(jsonPath("$.transactions[0].method").value(is(transaction0.getDescription().getMethod())))
                .andExpect(jsonPath("$.transactions[0].transferValue").value(is(expectedString(transaction0.getDescription().getTransferValue()))))
                .andExpect(jsonPath("$.transactions[0].tip").value(is(expectedString(transaction0.getDescription().getTip()))))
                .andExpect(jsonPath("$.transactions[0].fee").value(is(expectedString(transaction0.getDescription().getFee()))))
                .andExpect(jsonPath("$.transactions[0].reserved").value(is(expectedString(transaction0.getDescription().getReserved()))))
                .andExpect(jsonPath("$.transactions[0].total").value(is(expectedString(total0))))
                .andExpect(jsonPath("$.transactions[0].createdOn").value(is(TIMESTAMP.toString())))
                .andExpect(jsonPath("$.transactions[1].from").value(is(address2.getRawValue())))
                .andExpect(jsonPath("$.transactions[1].to").value(is(address1.getRawValue())))
                .andExpect(jsonPath("$.transactions[1].pallet").value(is(transaction1.getDescription().getPallet())))
                .andExpect(jsonPath("$.transactions[1].method").value(is(transaction1.getDescription().getMethod())))
                .andExpect(jsonPath("$.transactions[1].transferValue").value(is(expectedString(transaction1.getDescription().getTransferValue()))))
                .andExpect(jsonPath("$.transactions[1].tip").value(is(expectedString(transaction1.getDescription().getTip()))))
                .andExpect(jsonPath("$.transactions[1].fee").value(is(expectedString(transaction1.getDescription().getFee()))))
                .andExpect(jsonPath("$.transactions[1].reserved").value(is(expectedString(transaction1.getDescription().getReserved()))))
                .andExpect(jsonPath("$.transactions[1].total").value(is(expectedString(total1))))
                .andExpect(jsonPath("$.transactions[1].createdOn").value(is(TIMESTAMP.toString())))
        ;
    }
    
    private String expectedString(BigInteger value) {
        return value.toString();
    }

    private Transaction transaction(Ss58Address from, Ss58Address to, String pallet, String method, long transferValue, long tip, long fee, long reserved) {
        var description = TransactionDescription.builder()
                .from(from)
                .to(to)
                .createdOn(TIMESTAMP)
                .pallet(pallet)
                .method(method)
                .transferValue(BigInteger.valueOf(transferValue))
                .tip(BigInteger.valueOf(tip))
                .fee(BigInteger.valueOf(fee))
                .reserved(BigInteger.valueOf(reserved))
                .build();
        var transaction = mock(Transaction.class);
        when(transaction.getDescription()).thenReturn(description);
        return transaction;
    }
}
