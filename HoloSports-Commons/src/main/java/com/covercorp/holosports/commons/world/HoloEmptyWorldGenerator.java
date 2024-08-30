package com.covercorp.holosports.commons.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class HoloEmptyWorldGenerator extends ChunkGenerator {
    @Override
    public void generateSurface(final @NotNull WorldInfo info, final @NotNull Random random, final int x, final int z, final @NotNull ChunkGenerator.ChunkData data) {
        for (int y = info.getMinHeight(); y < info.getMaxHeight(); y++) {
            if (x == 0 && z == 0) data.setBlock(0, 69, 0, Material.BEDROCK);
            else data.setBlock(x, y, z, Material.AIR);
        }
    }

    @Override
    public void generateBedrock(final @NotNull WorldInfo info, final @NotNull Random random, final int x, final int z, final @NotNull ChunkGenerator.ChunkData data) {
        for (int y = info.getMinHeight(); y < info.getMaxHeight(); y++) {
            data.setBlock(x, y, z, Material.AIR);
        }
    }

    @Override
    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0.0, 70.0, 0.0);
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }
}
