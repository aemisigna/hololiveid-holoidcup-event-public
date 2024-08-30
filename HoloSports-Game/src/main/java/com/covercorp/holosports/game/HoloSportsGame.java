package com.covercorp.holosports.game;

import com.covercorp.holosports.game.listener.CosmeticJoinListener;
import com.covercorp.holosports.game.minigame.MiniGame;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.type.MiniGameType;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class HoloSportsGame extends JavaPlugin {
    @Getter(AccessLevel.PUBLIC) private static HoloSportsGame holoSportsGame;

    @Getter(AccessLevel.PUBLIC) private MiniGameType gameType;

    @Getter(AccessLevel.PUBLIC) private MiniGame miniGame;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        holoSportsGame = this;

        // Print the incredible ascii art, and the version.
        final String ascii = """
                  
                  
                 ░█░█░█▀█░█░░░█▀█░█░░░▀█▀░█░█░█▀▀
                 ░█▀█░█░█░█░░░█░█░█░░░░█░░▀▄▀░█▀▀                          hololive holoID Minecraft Game Engine
                 ░▀░▀░▀▀▀░▀▀▀░▀▀▀░▀▀▀░▀▀▀░░▀░░▀▀▀                                   Version: %s

                 ░█▄█░█▀▀░░░█▀▀░█▀█░█▄█░█▀▀░░░█▀▀░█▀█░█▀▀░▀█▀░█▀█░█▀▀                    Aemis-Yu
                 ░█░█░█░░░░░█░█░█▀█░█░█░█▀▀░░░█▀▀░█░█░█░█░░█░░█░█░█▀▀           Copyright 2023 - COVER Corp
                 ░▀░▀░▀▀▀░░░▀▀▀░▀░▀░▀░▀░▀▀▀░░░▀▀▀░▀░▀░▀▀▀░▀▀▀░▀░▀░▀▀▀

                """;

        getLogger().info(String.format(ascii, getDescription().getVersion()));

        getServer().getPluginManager().registerEvents(new CosmeticJoinListener(this), this);

        gameType = MiniGameType.valueOf(getConfig().getString("game-type"));

        switch (gameType) {
            case SOCCER -> miniGame = new SoccerMiniGame(this);
            case BADMINTON -> miniGame = new BadmintonMiniGame(this);
            case BENTENGAN -> miniGame = new BentenganMiniGame(this);
            case POTATO -> miniGame = new PotatoMiniGame(this);
            case TUG -> miniGame = new TugMiniGame(this);
            default -> miniGame = null;
        }
        if (miniGame == null) {
            getLogger().severe("ERROR! Invalid game type! Please check your config.yml! The server will now shut down!");
            getServer().shutdown();
            return;
        }

        miniGame.onGameLoad();
    }

    @Override
    public void onDisable() {
        if (miniGame != null) miniGame.onGameUnload();

        miniGame = null;
    }
}
