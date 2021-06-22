package logion.backend.model.protectionrequest;

import logion.backend.annotation.ValueObject;

@ValueObject
public enum ProtectionRequestStatus {
    PENDING,
    ACTIVATED
}
