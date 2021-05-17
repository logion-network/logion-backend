package logion.backend.model.tokenizationrequest;

import logion.backend.annotation.ValueObject;

@ValueObject
public enum TokenizationRequestStatus {
    PENDING,
    REJECTED
}
