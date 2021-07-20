package logion.backend.model.transfer;

import logion.backend.api.TransferController;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransferController.class)
class TransferWebTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void fetchTransfers() throws Exception {
        String address = "abcd1234";
        var request = new JSONObject()
                .put("address", address);


        mvc.perform(put("/transfer")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transfers[0].from").value(is(address)))
                .andExpect(jsonPath("$.transfers[1].to").value(is(address)))
                ;


    }
}
