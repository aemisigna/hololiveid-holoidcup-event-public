package com.covercorp.holosports.game.minigame;

import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.type.MiniGameType;
import fr.minuskube.inv.InventoryManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class MiniGame {
    @Getter(AccessLevel.PUBLIC) private final HoloSportsGame holoSportsGame;

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private InventoryManager inventoryManager;

    @Getter(AccessLevel.PUBLIC) private final FileConfiguration gameConfiguration;

    public MiniGame(final HoloSportsGame holoSportsGame, final MiniGameType type) {
        this.holoSportsGame = holoSportsGame;

        final String configFileNaming = type.name().toLowerCase() + ".yml";
        final File customConfigFile = new File(holoSportsGame.getDataFolder(), configFileNaming);

        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            holoSportsGame.saveResource(configFileNaming, false);
        }

        gameConfiguration = YamlConfiguration.loadConfiguration(customConfigFile);

        holoSportsGame.getLogger().info("Selected minigame is " + holoSportsGame.getGameType() + ". The config file for this minigame is " + configFileNaming + ". Loading minigame...");
    }

    public abstract void onGameLoad();

    public abstract void onGameUnload();
}
