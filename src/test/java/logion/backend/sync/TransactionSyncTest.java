package logion.backend.sync;

import java.util.Collections;
import java.util.Optional;
import logion.backend.chain.BlockService;
import logion.backend.chain.view.Block;
import logion.backend.commands.TransactionCommands;
import logion.backend.model.sync.SyncPointRepository;
import logion.backend.model.transaction.TransactionDescription;
import logion.backend.model.transaction.TransactionFactory;
import logion.backend.sync.vo.BlockWithTransactions;
import logion.backend.sync.vo.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
class TransactionSyncTest {

    @Autowired
    private TransactionSync transactionSync;

    @MockBean
    private BlockService blockService;
    @MockBean
    private TransactionExtractor transactionExtractor;
    @MockBean
    private TransactionFactory transactionFactory;
    @MockBean
    private TransactionCommands transactionCommands;
    @MockBean
    private SyncPointRepository syncPointRepository;

    @BeforeEach
    void init() {
        reset(blockService, transactionExtractor, transactionFactory, transactionCommands, syncPointRepository);
    }

    @Test
    void nothingToDo() {
        // Given
        var head = 12345L;
        when(blockService.getHeadBlockNumber()).thenReturn(head);
        when(syncPointRepository.findTransactionSyncPoint()).thenReturn(head);

        // When
        transactionSync.syncTransactions();

        // Then
        verify(blockService).getHeadBlockNumber();
        verify(syncPointRepository).findTransactionSyncPoint();
        verifyNoMoreInteractions(blockService, transactionExtractor, transactionFactory, transactionCommands, syncPointRepository);
    }

    @Test
    void nBlocksToSync() {
        // Given
        var head = 10002L;
        var n = 5;
        when(blockService.getHeadBlockNumber()).thenReturn(head);
        var block = mock(Block.class);
        when(blockService.getBlock(anyLong())).thenReturn(block);
        when(syncPointRepository.findTransactionSyncPoint()).thenReturn(head - n);

        var transaction = Transaction.builder().build();
        var transactions = Collections.singletonList(transaction);
        var blockWithTransaction = BlockWithTransactions.builder()
                .transactions(transactions)
                .build();
        when(transactionExtractor.extractBlockWithTransaction(block)).thenReturn(Optional.of(blockWithTransaction));

        // When
        transactionSync.syncTransactions();

        // Then
        verify(blockService).getHeadBlockNumber();
        verify(syncPointRepository).findTransactionSyncPoint();
        verify(blockService, times(n)).getBlock(anyLong());
        verify(transactionExtractor, times(n)).extractBlockWithTransaction(block);
        verify(transactionFactory, times(n)).newTransaction(anyLong(), anyInt(), any(TransactionDescription.class));
        verify(transactionCommands, times(n)).addTransactions(anyLong(), anyList());
        verifyNoMoreInteractions(blockService, transactionExtractor, transactionFactory, transactionCommands, syncPointRepository);

    }

    @Test
    void eraseCacheAndSyncFromStart() {
        // Given
        var head = 5L;
        var n = 5;
        when(blockService.getHeadBlockNumber()).thenReturn(head);
        var block = mock(Block.class);
        when(blockService.getBlock(anyLong())).thenReturn(block);
        when(syncPointRepository.findTransactionSyncPoint()).thenReturn(789789L);

        var transaction = Transaction.builder().build();
        var transactions = Collections.singletonList(transaction);
        var blockWithTransaction = BlockWithTransactions.builder()
                .transactions(transactions)
                .build();
        when(transactionExtractor.extractBlockWithTransaction(block)).thenReturn(Optional.of(blockWithTransaction));

        // When
        transactionSync.syncTransactions();

        // Then
        verify(blockService).getHeadBlockNumber();
        verify(syncPointRepository).findTransactionSyncPoint();
        verify(transactionCommands).deleteAllTransactions();
        verify(blockService, times(n)).getBlock(anyLong());
        verify(transactionExtractor, times(n)).extractBlockWithTransaction(block);
        verify(transactionFactory, times(n)).newTransaction(anyLong(), anyInt(), any(TransactionDescription.class));
        verify(transactionCommands, times(n)).addTransactions(anyLong(), anyList());
        verifyNoMoreInteractions(blockService, transactionExtractor, transactionFactory, transactionCommands, syncPointRepository);

    }

}
