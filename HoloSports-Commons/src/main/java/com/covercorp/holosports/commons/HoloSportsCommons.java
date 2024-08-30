package com.covercorp.holosports.commons;

import com.covercorp.holosports.commons.world.HoloEmptyWorldGenerator;
import com.covercorp.holosports.shared.HoloSportsShared;
import com.covercorp.holosports.shared.config.SharedConfig;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter(AccessLevel.PUBLIC)
public final class HoloSportsCommons extends JavaPlugin {
    @Getter(AccessLevel.PUBLIC) private static HoloSportsCommons coreCommons;
    private HoloSportsShared coreShared;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        coreShared = new HoloSportsShared(
                new SharedConfig(
                        getConfig().getString("database.address"),
                        getConfig().getInt("database.port"),
                        getConfig().getString("database.database"),
                        getConfig().getString("database.user"),
                        getConfig().getString("database.password")
                )
        );

        coreCommons = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        coreShared = null;
        coreCommons = null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(final @NotNull String worldName, final @Nullable String id) {
        return new HoloEmptyWorldGenerator();
    }
}
