package logion.backend.model.sync;

import javax.persistence.Entity;
import javax.persistence.Id;
import logion.backend.annotation.AggregateRoot;
import lombok.Getter;

@AggregateRoot
@Entity(name = "sync_point")
public class SyncPoint {

    @Id
    String name;

    @Getter
    long blockNumber;

    SyncPoint(String name, long blockNumber) {
        this.name = name;
        this.blockNumber = blockNumber;
    }

    SyncPoint() {
    }
}
