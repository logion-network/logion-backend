package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;

@ValueObject
public enum LegalOfficerDecisionStatus {
    PENDING,
    REJECTED,
    ACCEPTED
}
