package com.covercorp.holosports.commons.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class BlockUtil {
    public static List<Block> getNearbyBlocks(final Location location, final Material blockType, final int radius) {
        final World world = location.getWorld();
        if (world == null) return List.of();
        
        int layer = (radius * 2) + 1;
        final List<Block> blocks = new ArrayList<>(layer * layer * layer);
        for (double x = location.getX() - radius; x <= location.getX() + radius; x++) {
            for (double y = location.getY() - (radius); y <= location.getY() + radius; y++) {
                for (double z = location.getZ() - radius; z <= location.getZ() + radius; z++) {
                    if (world.getBlockAt((int) x,(int) y,(int) z).getType() == blockType) {
                        blocks.add(world.getBlockAt((int) x, (int) y, (int) z));
                    }
                }
            }
        }
        return blocks;
    }
}
