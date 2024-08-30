package com.covercorp.holosports.hub.config;

import com.covercorp.holosports.hub.HoloSportsHub;
import com.covercorp.holosports.hub.config.game.GameConfig;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ConfigHelper {
    private final FileConfiguration configuration;
    private final ConfigurationSection npcSection;

    public ConfigHelper(final HoloSportsHub holoSportsHub, final FileConfiguration configuration) {
        if (!new File(holoSportsHub.getDataFolder() + "/config.yml").exists()) {
            holoSportsHub.saveDefaultConfig();
        }

        this.configuration = configuration;

        this.npcSection = configuration.getConfigurationSection("npcs");
    }

    public List<GameConfig> getGameNpcConfigs() {
        final List<GameConfig> configs = new ArrayList<>();

        npcSection.getKeys(false).forEach(key -> {
            final GameConfig gameConfig = new GameConfig(
                    npcSection.getString(key + ".display"),
                    npcSection.getString(key + ".serverId"),
                    npcSection.getLocation(key + ".location"),
                    new ItemStack(Material.valueOf(npcSection.getString(key + ".item"))),
                    npcSection.getInt(key + ".model"),
                    ChatColor.valueOf(npcSection.getString(key + ".color"))
            );

            configs.add(gameConfig);
        });

        return configs;
    }

    public Location getSpawnLocation() {
        return configuration.getLocation("hub.spawn");
    }
    public String getMessageOfTheDay() {
        return configuration.getString("hub.motd");
    }
}