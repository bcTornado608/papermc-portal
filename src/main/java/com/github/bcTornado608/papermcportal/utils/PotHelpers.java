package com.github.bcTornado608.papermcportal.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.google.common.collect.ImmutableSet;

public class PotHelpers {

    public static Set<Block> detectPot(Material potMaterial, Material fillerMaterial, Block start, int maxVolume){
        Set<Block> visitedBlocks = new HashSet<>();
        Set<Block> activeBlocks = new HashSet<>();

        activeBlocks.add(start);

        boolean isPot = true;

        while (!activeBlocks.isEmpty() && isPot) {
            Set<Block> newActiveBlocks = new HashSet<>();
            for (Block block : activeBlocks) {
                Set<BlockFace> lowerFaces = ImmutableSet.of(BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
                
                // check lower blocks
                for (BlockFace lowerFace : lowerFaces) {
                    Block sideBlock = block.getRelative(lowerFace);
                    if(sideBlock.getType() == fillerMaterial){
                        newActiveBlocks.add(sideBlock);
                    } else if (sideBlock.getType() == potMaterial){
                        // do nothing for pot block
                    } else {
                        isPot = false;
                        break;
                    }
                }

                // check top block
                Block topBlock = block.getRelative(BlockFace.UP);
                if(topBlock.getType() == fillerMaterial){
                    newActiveBlocks.add(topBlock);
                } else if (topBlock.getType() == Material.AIR || topBlock.getType() == potMaterial){
                    // do nothing for air on top
                } else {
                    isPot = false;
                    break;
                }
            }
            visitedBlocks.addAll(activeBlocks);
            newActiveBlocks.removeAll(visitedBlocks);
            activeBlocks = newActiveBlocks;
            
            if(visitedBlocks.size() > maxVolume){
                isPot = false;
                break;
            }
        }

        if(isPot){
            return visitedBlocks;
        }
        return Collections.emptySet();
    }
}
