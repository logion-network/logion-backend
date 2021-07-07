package logion.backend.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import logion.backend.api.ProtectionRequestController;
import logion.backend.commands.ProtectionRequestCommands;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Signature;
import logion.backend.model.Ss58Address;
import logion.backend.model.protectionrequest.FetchProtectionRequestsSpecification;
import logion.backend.model.protectionrequest.LegalOfficerDecisionDescription;
import logion.backend.model.protectionrequest.LegalOfficerDecisionStatus;
import logion.backend.model.protectionrequest.PostalAddress;
import logion.backend.model.protectionrequest.ProtectionRequestAggregateRoot;
import logion.backend.model.protectionrequest.ProtectionRequestDescription;
import logion.backend.model.protectionrequest.ProtectionRequestFactory;
import logion.backend.model.protectionrequest.ProtectionRequestRepository;
import logion.backend.model.protectionrequest.ProtectionRequestStatus;
import logion.backend.model.protectionrequest.UserIdentity;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static logion.backend.testutil.MockSignature.signatureVerifyMock;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @SuppressWarnings("unchecked")
    void createProtectionRequest(String request,
                                 ResultMatcher resultMatcher,
                                 int numberOfInvocation,
                                 ProtectionRequestDescription expectedProtectionRequestDescription,
                                 List<LegalOfficerDecisionDescription> expectedLegalOfficerDecisionDescriptions)
            throws Exception {

        var protectionRequest = mock(ProtectionRequestAggregateRoot.class);
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
                    expectedProtectionRequestDescription.isRecovery(),
                    expectedProtectionRequestDescription.getAddressToRecover().map(Ss58Address::getRawValue).orElse(""),
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
        return Stream.of(
                Arguments.of(validProtectionRequest(), status().isOk(), 1, protectionRequestDescription(), legalOfficerDecisionDescriptions()),
                Arguments.of(validRecoveryRequest(), status().isOk(), 1, recoveryRequestDescription(), legalOfficerDecisionDescriptions()),
                Arguments.of("", status().isBadRequest(), 0, null, null)
        );
    }

    private static List<LegalOfficerDecisionDescription> legalOfficerDecisionDescriptions() {
        BiFunction<Ss58Address, LegalOfficerDecisionStatus, LegalOfficerDecisionDescription> decision = (address,status) -> LegalOfficerDecisionDescription.builder()
                .legalOfficerAddress(address)
                .rejectReason(status == LegalOfficerDecisionStatus.REJECTED ? REJECT_REASON : null)
                .createdOn(TIMESTAMP)
                .decisionOn(status == LegalOfficerDecisionStatus.REJECTED ? TIMESTAMP : null)
                .status(status)
                .build();

        return List.of(
                decision.apply(DefaultAddresses.ALICE, LegalOfficerDecisionStatus.PENDING),
                decision.apply(DefaultAddresses.BOB, LegalOfficerDecisionStatus.REJECTED)
        );
    }

    private static ProtectionRequestDescription protectionRequestDescription() {
        return protectionRequestDescription(REQUESTER_ADDRESS);
    }

    private static ProtectionRequestDescription protectionRequestDescription(String requesterAddress) {
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
        return ProtectionRequestDescription.builder()
                .requesterAddress(new Ss58Address(requesterAddress))
                .userIdentity(userIdentity)
                .userPostalAddress(postalAddress)
                .createdOn(TIMESTAMP)
                .build();
    }

    private static ProtectionRequestDescription recoveryRequestDescription() {
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
        return ProtectionRequestDescription.builder()
                .requesterAddress(new Ss58Address(REQUESTER_ADDRESS))
                .userIdentity(userIdentity)
                .userPostalAddress(postalAddress)
                .createdOn(TIMESTAMP)
                .isRecovery(true)
                .addressToRecover(Optional.of(new Ss58Address(ADDRESS_TO_RECOVER)))
                .build();
    }

    private static String validProtectionRequest() throws JSONException {
        var validRequest = new JSONObject();
        validRequest.put("legalOfficerAddresses", new String[]{DefaultAddresses.ALICE.getRawValue(), DefaultAddresses.BOB.getRawValue()});
        validRequest.put("requesterAddress", REQUESTER_ADDRESS);
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

        validRequest.put("isRecovery", false);
        validRequest.put("addressToRecover", "");

        return validRequest.toString(2);
    }

    private static String validRecoveryRequest() throws JSONException {
        var validRequest = new JSONObject();
        validRequest.put("legalOfficerAddresses", new String[]{DefaultAddresses.ALICE.getRawValue(), DefaultAddresses.BOB.getRawValue()});
        validRequest.put("requesterAddress", REQUESTER_ADDRESS);
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

        validRequest.put("isRecovery", true);
        validRequest.put("addressToRecover", ADDRESS_TO_RECOVER);

        return validRequest.toString(2);
    }

    @ParameterizedTest
    @MethodSource("signatureValidityWithStatus")
    void acceptProtectionRequestWithWrongSignature(boolean signatureVerifyResult, ResultMatcher matcher) throws Exception {
        var requestId = UUID.randomUUID();

        var existingRequest = mock(ProtectionRequestAggregateRoot.class);
        when(protectionRequestRepository.findById(requestId))
                .thenReturn(Optional.of(existingRequest));

        var requestBody = new JSONObject();
        requestBody.put("signature", SIGNATURE);
        requestBody.put("signedOn", LocalDateTime.now());
        requestBody.put("legalOfficerAddress", DefaultAddresses.BOB.getRawValue());

        var approving = signatureVerifyMock(
                DefaultAddresses.BOB,
                "protection-request",
                "accept",
                signatureVerifyResult,
                requestId.toString());
        when(signature.verify(SIGNATURE)).thenReturn(approving);

        mvc.perform(post("/protection-request/" + requestId + "/accept")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(requestBody.toString()))
                .andExpect(matcher);
        if (signatureVerifyResult) {
            verify(protectionRequestCommands).acceptProtectionRequest(eq(requestId), eq(DefaultAddresses.BOB), isA(LocalDateTime.class));
        }
        verifyNoMoreInteractions(protectionRequestCommands);
    }

    @ParameterizedTest
    @MethodSource("signatureValidityWithStatus")
    void rejectProtectionRequestWithWrongSignature(boolean signatureVerifyResult, ResultMatcher matcher) throws Exception {
        var requestId = UUID.randomUUID();

        var existingRequest = mock(ProtectionRequestAggregateRoot.class);
        when(protectionRequestRepository.findById(requestId))
                .thenReturn(Optional.of(existingRequest));

        var requestBody = new JSONObject();
        requestBody.put("signature", SIGNATURE);
        requestBody.put("signedOn", LocalDateTime.now());
        requestBody.put("legalOfficerAddress", DefaultAddresses.BOB.getRawValue());
        requestBody.put("rejectReason", REJECT_REASON);

        var approving = signatureVerifyMock(
                DefaultAddresses.BOB,
                "protection-request",
                "reject",
                signatureVerifyResult,
                requestId.toString(),
                REJECT_REASON);
        when(signature.verify(SIGNATURE)).thenReturn(approving);

        mvc.perform(post("/protection-request/" + requestId + "/reject")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(requestBody.toString()))
                .andExpect(matcher);
        if (signatureVerifyResult) {
            verify(protectionRequestCommands).rejectProtectionRequest(eq(requestId), eq(DefaultAddresses.BOB), eq(REJECT_REASON), isA(LocalDateTime.class));
        }
        verifyNoMoreInteractions(protectionRequestCommands);
    }

    @Test
    void fetchProtectionRequests() throws Exception {

        var requestBody = new JSONObject();
        requestBody.put("requesterAddress", REQUESTER_ADDRESS);
        requestBody.put("legalOfficerAddress", DefaultAddresses.ALICE.getRawValue());
        requestBody.put("decisionStatuses", new String[]{"ACCEPTED", "REJECTED"});

        var id = UUID.randomUUID();
        var protectionRequest = mock(ProtectionRequestAggregateRoot.class);
        when(protectionRequest.getDescription()).thenReturn(protectionRequestDescription());
        when(protectionRequest.getLegalOfficerDecisionDescriptions()).thenReturn(legalOfficerDecisionDescriptions());
        when(protectionRequest.getId()).thenReturn(id);
        when(protectionRequest.getStatus()).thenReturn(ProtectionRequestStatus.PENDING);

        when(protectionRequestRepository.findBy(any(FetchProtectionRequestsSpecification.class))).thenReturn(singletonList(protectionRequest));

        mvc.perform(put("/protection-request/")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(requestBody.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requests.length()").value(is(1)))
                .andExpect(jsonPath("$.requests[0].id").value(is(id.toString())))
                .andExpect(jsonPath("$.requests[0].requesterAddress").value(is(REQUESTER_ADDRESS)))
                .andExpect(jsonPath("$.requests[0].userIdentity.firstName").value(is("John")))
                .andExpect(jsonPath("$.requests[0].userIdentity.lastName").value(is("Doe")))
                .andExpect(jsonPath("$.requests[0].userIdentity.email").value(is("john.doe@logion.network")))
                .andExpect(jsonPath("$.requests[0].userIdentity.phoneNumber").value(is("+1234")))
                .andExpect(jsonPath("$.requests[0].userPostalAddress.line1").value(is("Place de le République Française, 10")))
                .andExpect(jsonPath("$.requests[0].userPostalAddress.line2").value(is("boite 15")))
                .andExpect(jsonPath("$.requests[0].userPostalAddress.postalCode").value(is("4000")))
                .andExpect(jsonPath("$.requests[0].userPostalAddress.city").value(is("Liège")))
                .andExpect(jsonPath("$.requests[0].userPostalAddress.country").value(is("Belgium")))
                .andExpect(jsonPath("$.requests[0].decisions[0].legalOfficerAddress").value(is(DefaultAddresses.ALICE.getRawValue())))
                .andExpect(jsonPath("$.requests[0].decisions[0].status").value(is("PENDING")))
                .andExpect(jsonPath("$.requests[0].decisions[0].createdOn").value(TIMESTAMP.toString()))
                .andExpect(jsonPath("$.requests[0].decisions[1].legalOfficerAddress").value(is(DefaultAddresses.BOB.getRawValue())))
                .andExpect(jsonPath("$.requests[0].decisions[1].status").value(is("REJECTED")))
                .andExpect(jsonPath("$.requests[0].decisions[1].rejectReason").value(is(REJECT_REASON)))
                .andExpect(jsonPath("$.requests[0].decisions[1].createdOn").value(TIMESTAMP.toString()))
                .andExpect(jsonPath("$.requests[0].decisions[1].decisionOn").value(TIMESTAMP.toString()))
                .andExpect(jsonPath("$.requests[0].createdOn").value(TIMESTAMP.toString()))
                .andExpect(jsonPath("$.requests[0].isRecovery").exists())
                .andExpect(jsonPath("$.requests[0].addressToRecover").exists())
                .andExpect(jsonPath("$.requests[0].status").value(is("PENDING")))
                ;

        var argumentCaptor = ArgumentCaptor.forClass(FetchProtectionRequestsSpecification.class);
        verify(protectionRequestRepository).findBy(argumentCaptor.capture());
        FetchProtectionRequestsSpecification actualSpecification = argumentCaptor.getValue();

        assertThat(actualSpecification.getExpectedRequesterAddress(), is(Optional.of(new Ss58Address(REQUESTER_ADDRESS))));
        assertThat(actualSpecification.getExpectedLegalOfficer(), is(Optional.of(DefaultAddresses.ALICE)));
        assertThat(actualSpecification.getExpectedDecisionStatuses(), hasItems(LegalOfficerDecisionStatus.ACCEPTED, LegalOfficerDecisionStatus.REJECTED));
    }

    @ParameterizedTest
    @MethodSource
    void checkProtection(boolean expectedResult, String userAddress, List<String> legalOfficerAddress, List<ProtectionRequestAggregateRoot> requests, int numberOfRepoCall) throws Exception {

        when(protectionRequestRepository.findBy(any(FetchProtectionRequestsSpecification.class))).thenReturn(requests);

        var requestBody = new JSONObject();
        requestBody.put("userAddress", userAddress);
        requestBody.put("legalOfficerAddresses", legalOfficerAddress);
        requestBody.put("unexpectedAttributeToCheckRobustness", "something useless");

        mvc.perform(put("/protection-request/check")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(requestBody.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protection").value(is(expectedResult)));

            verify(protectionRequestRepository, times(numberOfRepoCall)).findBy(any(FetchProtectionRequestsSpecification.class));
    }

    private static final String SIGNATURE = "signature";
    private static final String REJECT_REASON = "Illegal";
    private static final String REQUESTER_ADDRESS = "5H4MvAsobfZ6bBCDyj5dsrWYLrA8HrRzaqa9p61UXtxMhSCY";
    private static final String ADDRESS_TO_RECOVER = "5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.parse("2021-06-10T16:25:23.668294");

    @SuppressWarnings("unused")
    private static Stream<Arguments> signatureValidityWithStatus() {
        return Stream.of(
                Arguments.of(true, status().isOk()),
                Arguments.of(false, status().isBadRequest())
        );
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> checkProtection() {
        List<String> alice = List.of(DefaultAddresses.ALICE.getRawValue());
        List<String> aliceAndBob = List.of(DefaultAddresses.ALICE.getRawValue(), DefaultAddresses.BOB.getRawValue());
        return Stream.of(
                // valid requests
                Arguments.of(true, REQUESTER_ADDRESS, alice, singletonList(mock(ProtectionRequestAggregateRoot.class)), 1),
                Arguments.of(false, REQUESTER_ADDRESS, alice, emptyList(), 1),
                Arguments.of(true, REQUESTER_ADDRESS, aliceAndBob, singletonList(mock(ProtectionRequestAggregateRoot.class)), 2),
                Arguments.of(false, REQUESTER_ADDRESS, aliceAndBob, emptyList(), 1), // If Alice not checked, we won't even try to check Bob
                // invalid requests
                Arguments.of(false, null, aliceAndBob, null, 0),
                Arguments.of(false, "", aliceAndBob, null, 0),
                Arguments.of(false, REQUESTER_ADDRESS, emptyList(), null, 0),
                Arguments.of(false, REQUESTER_ADDRESS, null, null, 0)
        );
    }

    @Test
    void checkActivation() throws Exception {

        var requestId = UUID.randomUUID();

        var protectionRequest = mock(ProtectionRequestAggregateRoot.class);
        when(protectionRequest.getDescription()).thenReturn(protectionRequestDescription());
        when(protectionRequest.getLegalOfficerDecisionDescriptions()).thenReturn(legalOfficerDecisionDescriptions());
        when(protectionRequest.getId()).thenReturn(requestId);
        when(protectionRequest.getStatus()).thenReturn(ProtectionRequestStatus.ACTIVATED);

        when(protectionRequestCommands.checkAndSetProtectionRequestActivation(requestId))
                .thenReturn(protectionRequest);

        var requestBody = new JSONObject();
        requestBody.put("signature", SIGNATURE);
        requestBody.put("signedOn", LocalDateTime.now());
        requestBody.put("userAddress", DefaultAddresses.BOB.getRawValue());

        var approving = signatureVerifyMock(
                DefaultAddresses.BOB,
                "protection-request",
                "check-activation",
                true,
                requestId.toString());
        when(signature.verify(SIGNATURE)).thenReturn(approving);

        mvc.perform(post("/protection-request/" + requestId + "/check-activation")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(requestBody.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(requestId.toString())))
                .andExpect(jsonPath("$.status").value(is("ACTIVATED")));

        verify(protectionRequestCommands).checkAndSetProtectionRequestActivation(requestId);

    }

    @Test
    void fetchRecoveryInfo() throws Exception {
        var requestId = UUID.randomUUID();

        var recovery = mock(ProtectionRequestAggregateRoot.class);

        when(recovery.getStatus()).thenReturn(ProtectionRequestStatus.ACTIVATED);
        when(recovery.getDescription()).thenReturn(recoveryRequestDescription());
        when(recovery.getId()).thenReturn(requestId);
        when(protectionRequestRepository.findById(requestId)).thenReturn(Optional.of(recovery));

        var toRecover = mock(ProtectionRequestAggregateRoot.class);
        when(toRecover.getStatus()).thenReturn(ProtectionRequestStatus.ACTIVATED);
        when(toRecover.getDescription()).thenReturn(protectionRequestDescription(ADDRESS_TO_RECOVER));
        when(protectionRequestRepository.findBy(FetchProtectionRequestsSpecification.builder()
                .expectedRequesterAddress(Optional.of(new Ss58Address(ADDRESS_TO_RECOVER)))
                .expectedProtectionRequestStatus(Optional.of(ProtectionRequestStatus.ACTIVATED))
                .build()))
                .thenReturn(singletonList(toRecover));

        mvc.perform(put("/protection-request/" + requestId + "/recovery-info")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recoveryAccount.requesterAddress").value(is(REQUESTER_ADDRESS)))
                .andExpect(jsonPath("$.recoveryAccount.addressToRecover").value(is(ADDRESS_TO_RECOVER)))
                .andExpect(jsonPath("$.recoveryAccount.isRecovery").value(is(true)))
                .andExpect(jsonPath("$.recoveryAccount.userIdentity.firstName").value(is("John")))
                .andExpect(jsonPath("$.recoveryAccount.userPostalAddress.postalCode").value(is("4000")))
                .andExpect(jsonPath("$.accountToRecover.requesterAddress").value(is(ADDRESS_TO_RECOVER)))
                .andExpect(jsonPath("$.accountToRecover.addressToRecover").value(is("")))
                .andExpect(jsonPath("$.accountToRecover.isRecovery").value(is(false)))
                .andExpect(jsonPath("$.accountToRecover.userIdentity.firstName").value(is("John")))
                .andExpect(jsonPath("$.accountToRecover.userPostalAddress.postalCode").value(is("4000")))
        ;

    }
}
