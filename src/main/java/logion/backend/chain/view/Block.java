package logion.backend.chain.view;

import logion.backend.annotation.View;
import lombok.Data;

@View
@Data
public class Block {

    long number;
    Extrinsic[] extrinsics;
}
