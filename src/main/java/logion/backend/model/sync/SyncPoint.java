package logion.backend.model.sync;

import java.time.LocalDateTime;
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
    long latestHeadBlockNumber;

    LocalDateTime updatedOn;

    SyncPoint(String name, long latestHeadBlockNumber) {
        this.name = name;
        this.latestHeadBlockNumber = latestHeadBlockNumber;
        this.updatedOn = LocalDateTime.now();
    }

    SyncPoint() {
    }
}
