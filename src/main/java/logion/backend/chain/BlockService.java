package logion.backend.chain;

import java.util.Optional;
import logion.backend.chain.view.Block;
import logion.backend.chain.view.BlockNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BlockService {

    @Autowired
    private RestTemplate sidecar;

    public long getHeadBlockNumber() {
        var url = "/blocks/head";
        var block = sidecar.getForEntity(url, BlockNumber.class);
        return Optional.ofNullable(block.getBody())
                .map(BlockNumber::getNumber)
                .orElse(0L);
    }

    public Block getBlock(long blockNumber) {
        var url = "/blocks/{blockId}";
        var block = sidecar.getForEntity(url, Block.class, blockNumber);
        return block.getBody();
    }

}
