package logion.backend.web;

import java.time.LocalDateTime;
import java.util.List;
import logion.backend.api.TransferController;
import logion.backend.model.Ss58Address;
import logion.backend.model.transfer.Transfer;
import logion.backend.model.transfer.TransferDescription;
import logion.backend.model.transfer.TransferRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
@WebMvcTest(TransferController.class)
class TransferWebTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.parse("2021-07-20T17:02:22.994321");

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransferRepository repository;

    @Test
    void fetchTransfers() throws Exception {

        Ss58Address address1 = new Ss58Address("abcd1234");
        Ss58Address address2 = new Ss58Address("efgh5678");

        var transfers = List.of(
                transfer(address1, address2),
                transfer(address2, address1)
        );
        when(repository.findByAddress(address1))
                .thenReturn(transfers);

        var request = new JSONObject()
                .put("address", address1.getRawValue());


        mvc.perform(put("/transfer")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transfers[0].from").value(is(address1.getRawValue())))
                .andExpect(jsonPath("$.transfers[0].to").value(is(address2.getRawValue())))
                .andExpect(jsonPath("$.transfers[0].value").value(is("123456")))
                .andExpect(jsonPath("$.transfers[0].createdOn").value(is(TIMESTAMP.toString())))
                .andExpect(jsonPath("$.transfers[1].from").value(is(address2.getRawValue())))
                .andExpect(jsonPath("$.transfers[1].to").value(is(address1.getRawValue())))
                .andExpect(jsonPath("$.transfers[1].value").value(is("123456")))
                .andExpect(jsonPath("$.transfers[1].createdOn").value(is(TIMESTAMP.toString())))
        ;
    }

    private Transfer transfer(Ss58Address from, Ss58Address to) {
        var description = TransferDescription.builder()
                .from(from)
                .to(to)
                .createdOn(TIMESTAMP)
                .value(123456)
                .build();
        var transfer = mock(Transfer.class);
        when(transfer.getDescription()).thenReturn(description);
        return transfer;
    }
}
