package logion.backend.model.sync;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SyncPointRepositoryTest {

    @Autowired
    private SyncPointRepository syncPointRepository;

    @Test
    void testUpdateThenFind() {
        long blockNumber = 123456L;
        syncPointRepository.saveTransactionSyncPoint(blockNumber);
        assertThat(syncPointRepository.findTransactionSyncPoint(), is(blockNumber));
    }
}
