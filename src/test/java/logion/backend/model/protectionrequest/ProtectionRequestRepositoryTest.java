package logion.backend.model.protectionrequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import logion.backend.model.DefaultAddresses;
import logion.backend.model.Ss58Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/protection_requests.sql")
class ProtectionRequestRepositoryTest {

    @Test
    void findBy() {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedDecisionStatuses(Set.of(
                        LegalOfficerDecisionStatus.ACCEPTED,
                        LegalOfficerDecisionStatus.REJECTED))
                .build();
        var results = repository.findBy(specification);
        assertThat(results.size(), is(4));
        assertThatAliceAcceptedOrRejected(results);
    }

    private void assertThatAliceAcceptedOrRejected(List<ProtectionRequestAggregateRoot> results) {
        results.stream()
                .flatMap(root -> root.getLegalOfficerDecisionDescriptions().stream())
                .filter(legalOfficerDecisionDescription -> legalOfficerDecisionDescription.getLegalOfficerAddress().equals(DefaultAddresses.ALICE))
                .map(LegalOfficerDecisionDescription::getStatus)
                .forEach(status -> assertThat(status, anyOf(
                        is(LegalOfficerDecisionStatus.ACCEPTED),
                        is(LegalOfficerDecisionStatus.REJECTED))));
    }

    @Autowired
    private ProtectionRequestRepository repository;

    @Test
    void findByRequesterAddress() {
        var requesterAddress = new Ss58Address("5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW");
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedRequesterAddress(Optional.of(requesterAddress))
                .build();
        var request = repository.findBy(specification);
        assertThat(request.size(), is(1));

        var protectionRequest = request.get(0);

        var legalOfficerDecisionDescriptions = protectionRequest.getLegalOfficerDecisionDescriptions();

        assertThat(legalOfficerDecisionDescriptions.size(), is(2));

        var legalOfficerAddresses = legalOfficerDecisionDescriptions.stream()
                .map(LegalOfficerDecisionDescription::getLegalOfficerAddress)
                .collect(Collectors.toSet());

        assertThat(legalOfficerAddresses, hasItems(DefaultAddresses.ALICE, DefaultAddresses.BOB));
    }

    @Test
    void findRecoveryOnly() {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedDecisionStatuses(Set.of(
                        LegalOfficerDecisionStatus.ACCEPTED,
                        LegalOfficerDecisionStatus.REJECTED))
                .kind(ProtectionRequestKind.RECOVERY)
                .build();
        var results = repository.findBy(specification);
        assertThat(results.size(), is(1));
        assertThatAliceAcceptedOrRejected(results);
        assertThatAllKind(results, ProtectionRequestKind.RECOVERY);
    }

    private void assertThatAllKind(List<ProtectionRequestAggregateRoot> results, ProtectionRequestKind kind) {
        results.stream()
            .map(ProtectionRequestAggregateRoot::getDescription)
            .map(ProtectionRequestDescription::isRecovery)
            .forEach(isRecovery -> assertThat(isRecovery, is(kind == ProtectionRequestKind.RECOVERY)));
    }

    @Test
    void findProtectionOnly() {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedDecisionStatuses(Set.of(
                        LegalOfficerDecisionStatus.ACCEPTED,
                        LegalOfficerDecisionStatus.REJECTED))
                .kind(ProtectionRequestKind.PROTECTION_ONLY)
                .build();
        var results = repository.findBy(specification);
        assertThat(results.size(), is(3));
        assertThatAliceAcceptedOrRejected(results);
        assertThatAllKind(results, ProtectionRequestKind.PROTECTION_ONLY);
    }

    @Test
    void findActivatedOnly() {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedProtectionRequestStatus(Optional.of(ProtectionRequestStatus.ACTIVATED))
                .build();
        var results = repository.findBy(specification);
        assertThat(results.size(), is(1));
        assertThatAllStatus(results, ProtectionRequestStatus.ACTIVATED);
    }

    @Test
    void findPendingOnly() {
        var specification = FetchProtectionRequestsSpecification.builder()
                .expectedLegalOfficer(Optional.of(DefaultAddresses.ALICE))
                .expectedProtectionRequestStatus(Optional.of(ProtectionRequestStatus.PENDING))
                .build();
        var results = repository.findBy(specification);
        assertThat(results.size(), is(4));
        assertThatAllStatus(results, ProtectionRequestStatus.PENDING);
    }

    @Test
    void protectionRequestNotFound() {
        var error = ProtectionRequestRepository.protectionRequestNotFound("Some info").get();
        assertThat(error.getMessage(), is("Some info"));
    }

    private void assertThatAllStatus(List<ProtectionRequestAggregateRoot> results, ProtectionRequestStatus status) {
        results.forEach(result -> assertThat(result.getStatus(), is(status)));
    }


}
