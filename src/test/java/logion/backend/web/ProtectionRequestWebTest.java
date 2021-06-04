package logion.backend.web;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import logion.backend.api.ProtectionRequestController;
import logion.backend.commands.ProtectionRequestCommands;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Signature;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.LegalOfficerDecisionDescription;
import logion.backend.model.protectionrequest.LegalOfficerDecisionStatus;
import logion.backend.model.protectionrequest.PostalAddress;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestDescription;
import logion.backend.model.protectionrequest.ProtectionRequestFactory;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import logion.backend.model.protectionrequest.UserIdentity;
import logion.backend.util.CollectionMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static logion.backend.testutil.MockSignature.signatureVerifyMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProtectionRequestController.class)
class ProtectionRequestWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProtectionRequestFactory protectionRequestFactory;

    @MockBean
    private ProtectionRequestCommands protectionRequestCommands;

    @MockBean
    private ProtectionRequestRepository protectionRequestRepository;

    @MockBean
    private Signature signature;

    @ParameterizedTest
    @MethodSource
    void createProtectionRequest(String request,
                                 ResultMatcher resultMatcher,
                                 int numberOfInvocation,
                                 ProtectionRequestDescription expectedProtectionRequestDescription,
                                 Set<LegalOfficerDecisionDescription> expectedLegalOfficerDecisionDescriptions)
            throws Exception {

        var protectionRequest = Mockito.mock(ProtectionRequestAggregateRoot.class);
        when(protectionRequest.getDescription()).thenReturn(expectedProtectionRequestDescription);
        when(protectionRequest.getLegalOfficerDecisionDescriptions()).thenReturn(expectedLegalOfficerDecisionDescriptions);


        when(protectionRequestFactory.newProtectionRequest(any(), isA(ProtectionRequestDescription.class), isA(Set.class)))
                .thenReturn(protectionRequest);
        when(protectionRequestCommands.addProtectionRequest(protectionRequest)).thenReturn(protectionRequest);

        if (expectedProtectionRequestDescription != null) {
            UserIdentity userIdentity = expectedProtectionRequestDescription.getUserIdentity();
            PostalAddress userPostalAddress = expectedProtectionRequestDescription.getUserPostalAddress();
            var approving = signatureVerifyMock(
                    expectedProtectionRequestDescription.getRequesterAddress(),
                    "protection-request",
                    "create",
                    true,
                    userIdentity.getFirstName(),
                    userIdentity.getLastName(),
                    userIdentity.getEmail(),
                    userIdentity.getPhoneNumber(),
                    userPostalAddress.getLine1(),
                    userPostalAddress.getLine2(),
                    userPostalAddress.getPostalCode(),
                    userPostalAddress.getCity(),
                    userPostalAddress.getCountry(),
                    new String[]{DefaultAddresses.ALICE.getRawValue(), DefaultAddresses.BOB.getRawValue()}
            );
            when(signature.verify("signature")).thenReturn(approving);

        }
        mvc.perform(post("/protection-request")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(request))
                .andExpect(resultMatcher);

        verify(protectionRequestCommands, times(numberOfInvocation)).addProtectionRequest(protectionRequest);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> createProtectionRequest() throws JSONException {
        var userIdentity = UserIdentity.builder()
                .email("john.doe@logion.network")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234")
                .build();
        var postalAddress = PostalAddress.builder()
                .line1("Place de le République Française, 10")
                .line2("boite 15")
                .postalCode("4000")
                .city("Liège")
                .country("Belgium")
                .build();
        var protectionRequestDescription = ProtectionRequestDescription.builder()
                .requesterAddress(new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW"))
                .userIdentity(userIdentity)
                .userPostalAddress(postalAddress)
                .createdOn(LocalDateTime.now())
                .build();
        Function<Ss58Address, LegalOfficerDecisionDescription> pendingDecision = address -> LegalOfficerDecisionDescription.builder()
                .legalOfficerAddress(address)
                .status(LegalOfficerDecisionStatus.PENDING)
                .build();
        Set<LegalOfficerDecisionDescription> legalOfficerDecisionDescriptions = CollectionMapper.mapArrayToSet(pendingDecision, DefaultAddresses.ALICE, DefaultAddresses.BOB);
        return Stream.of(
                Arguments.of(validRequest(), status().isOk(), 1, protectionRequestDescription, legalOfficerDecisionDescriptions),
                Arguments.of("", status().isBadRequest(), 0, null, null)
        );
    }

    private static String validRequest() throws JSONException {
        var validRequest = new JSONObject();
        validRequest.put("legalOfficerAddresses", new String[]{DefaultAddresses.ALICE.getRawValue(), DefaultAddresses.BOB.getRawValue()});
        validRequest.put("requesterAddress", "5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
        validRequest.put("signature", "signature");
        validRequest.put("signedOn", "2021-06-02T16:00:41.542839");

        var userIdentity = new JSONObject();
        userIdentity.put("firstName", "John");
        userIdentity.put("lastName", "Doe");
        userIdentity.put("phoneNumber", "+1234");
        userIdentity.put("email", "john.doe@logion.network");
        validRequest.put("userIdentity", userIdentity);

        var userPostalAddress = new JSONObject();
        userPostalAddress.put("line1", "Place de le République Française, 10");
        userPostalAddress.put("line2", "boite 15");
        userPostalAddress.put("postalCode", "4000");
        userPostalAddress.put("city", "Liège");
        userPostalAddress.put("country", "Belgium");
        validRequest.put("userPostalAddress", userPostalAddress);

        return validRequest.toString(2);
    }
}
