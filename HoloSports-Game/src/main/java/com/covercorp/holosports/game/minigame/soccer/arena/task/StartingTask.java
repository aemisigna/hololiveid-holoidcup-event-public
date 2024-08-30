package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.properties.SoccerMatchProperties;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import org.bukkit.Sound;

public final class StartingTask implements Runnable {
    final SoccerArena soccerArena;

    public StartingTask(final SoccerArena arena) {
        this.soccerArena = arena;
    }

    @Override
    public void run() {
        final SoccerMatchProperties properties = soccerArena.getSoccerMatchProperties();

        soccerArena.setState(SoccerMatchState.STARTING);
        properties.setStarting(true);

        if (properties.getStartingCountdown() == 5) {
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The game will start in 5 seconds, get prepared!");
            soccerArena.getArenaAnnouncer().sendGlobalSound(Sound.ITEM_GOAT_HORN_SOUND_0, 0.8F, 0.8F);


            soccerArena.getArenaAnnouncer().sendGlobalTitle("&bGet Ready!", "&7The match is starting!", 0, 60, 40);
        }

        if (properties.getStartingCountdown() == 0) {
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[!] Starting match...");
            soccerArena.start();

            return;
        }

        soccerArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
        soccerArena.getArenaAnnouncer().sendGlobalMessage("&eThe Soccer match will start in &b" + properties.getStartingCountdown() + " second(s)&e.");
        properties.decreaseCountdown();
    }
}