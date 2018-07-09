package co.nyzo.verifier;

import co.nyzo.verifier.util.NotificationUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockManagerMap {

    private static int iteration = 0;
    private static Map<Long, Block> blockMap = new HashMap<>();

    public static synchronized void addBlock(Block block) {

        if (block != null) {
            // Add the block to the map.
            blockMap.put(block.getBlockHeight(), block);

            // Periodically remove old blocks.
            if (iteration++ >= 10) {

                iteration = 0;

                long frozenEdgeHeight = BlockManager.getFrozenEdgeHeight();
                Block frozenEdge = BlockManager.frozenBlockForHeight(frozenEdgeHeight);
                if (frozenEdge.getCycleInformation() != null) {

                    long startHeight = frozenEdge.getCycleInformation().getWindowStartHeight();
                    startHeight -= 20;  // keep some extra blocks to aid in initialization of new verifiers

                    for (Long height : new HashSet<>(blockMap.keySet())) {
                        if (height != 0 && height < startHeight) {
                            blockMap.remove(height);
                        }
                    }
                }
            }
        }
    }

    public static Block blockForHeight(long blockHeight) {

        return blockMap.get(blockHeight);
    }

    // TODO: remove this; it is for debugging only
    public static int mapSize() {

        return blockMap.size();
    }
}
