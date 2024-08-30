package com.covercorp.holosports.hub;

import com.covercorp.holosports.hub.config.ConfigHelper;
import com.covercorp.holosports.hub.listener.HubListener;
import com.covercorp.holosports.hub.npc.NpcHelper;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

public final class HoloSportsHub extends JavaPlugin {
    @Getter(AccessLevel.PUBLIC) private static HoloSportsHub holoSportsHub;

    @Getter(AccessLevel.PUBLIC) private ConfigHelper configHelper;
    @Getter(AccessLevel.PUBLIC) private NpcHelper npcHelper;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        holoSportsHub = this;

        // Print the incredible ascii art, and the version.
        final String ascii = """
                  
                  
                 ░█░█░█▀█░█░░░█▀█░█░░░▀█▀░█░█░█▀▀
                 ░█▀█░█░█░█░░░█░█░█░░░░█░░▀▄▀░█▀▀                          hololive holoID Minecraft Game Engine
                 ░▀░▀░▀▀▀░▀▀▀░▀▀▀░▀▀▀░▀▀▀░░▀░░▀▀▀                                   Version: %s

                 ░█▄█░█▀▀░░░█▀▀░█▀█░█▄█░█▀▀░░░█▀▀░█▀█░█▀▀░▀█▀░█▀█░█▀▀                    Aemis-Yu
                 ░█░█░█░░░░░█░█░█▀█░█░█░█▀▀░░░█▀▀░█░█░█░█░░█░░█░█░█▀▀           Copyright 2023 - COVER. Corp
                 ░▀░▀░▀▀▀░░░▀▀▀░▀░▀░▀░▀░▀▀▀░░░▀▀▀░▀░▀░▀▀▀░▀▀▀░▀░▀░▀▀▀

                """;

        getLogger().info(String.format(ascii, getDescription().getVersion()));

        configHelper = new ConfigHelper(this, getConfig());

        npcHelper = new NpcHelper(this);

        getServer().getPluginManager().registerEvents(new HubListener(this), this);
    }

    @Override
    public void onDisable() {
        configHelper = null;

        holoSportsHub = null;
    }
}

