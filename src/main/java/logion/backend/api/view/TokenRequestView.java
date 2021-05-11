package logion.backend.api.view;

import java.util.UUID;

public class TokenRequestView {

    private final UUID id;

    public TokenRequestView(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
