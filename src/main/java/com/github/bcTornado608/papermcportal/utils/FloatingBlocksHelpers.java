package com.github.bcTornado608.papermcportal.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.block.Block;
import org.joml.Vector3i;

import com.github.bcTornado608.papermcportal.Portal;


public class FloatingBlocksHelpers {
    private static List<Vector3i> surroundingOffsets;

    private static void init(){
        // initialize surrounding offsets
        surroundingOffsets = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if(dx == 0 && dy == 0 && dz == 0){
                        continue;
                    }
                    surroundingOffsets.add(new Vector3i(dx, dy, dz));
                }
            }
        }
    }

    public static Set<Block> getConnectedFloatingBlocks(
                Set<Block> initialVisited,
                Set<Block> initialWaveFront,
                Predicate<? super Block> isInterestedBlock,
                Predicate<? super Block> isSupportingBlock,
                int maxCapacity){
        if(surroundingOffsets == null){
            init();
        }

        Set<Block> visited = new HashSet<>(initialVisited);
        Set<Block> waveFront = new HashSet<>(initialWaveFront);

        boolean done = false;
        boolean isFloating = true;

        // breadth first search
        Set<Block> tempVisited = new HashSet<>();
        Set<Block> tempWaveFront = new HashSet<>();
        while(!done){
            tempVisited.clear();
            tempWaveFront.clear();

            for (Block activeBlock : waveFront) {
                // search in 3x3x3 area, add all new blocks
                for (Vector3i offset : surroundingOffsets) {
                    Block currentBlock = activeBlock.getRelative(offset.x, offset.y, offset.z);
                    if(isInterestedBlock.test(currentBlock) && (!visited.contains(currentBlock))){
                        tempWaveFront.add(currentBlock);
                    }
                    else if(isSupportingBlock.test(currentBlock)){
                        // if there is a supporting block around, this is not floating, abort
                        done = true;
                        isFloating = false;
                        break;
                    }
                }
                tempVisited.add(activeBlock);
            }
            waveFront.clear();
            waveFront.addAll(tempWaveFront);
            visited.addAll(tempVisited);

            if(waveFront.isEmpty()){
                done = true;
            }

            // limit the max block can be cutted
            if(waveFront.size() + visited.size() > maxCapacity){
                Portal.getInstance().getLogger().warning(String.format("Connected log block exceeds max amount (%d), tree cut aborted.", maxCapacity));
                return Collections.emptySet();
            }
        }

        if(isFloating){
            return visited;
        }
        return Collections.emptySet();
    }
}
