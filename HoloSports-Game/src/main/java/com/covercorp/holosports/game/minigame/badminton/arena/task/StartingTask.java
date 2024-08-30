package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.properties.BadmintonMatchProperties;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import org.bukkit.Sound;

public final class StartingTask implements Runnable {
    final BadmintonArena badmintonArena;

    public StartingTask(final BadmintonArena arena) {
        this.badmintonArena = arena;
    }

    @Override
    public void run() {
        final BadmintonMatchProperties properties = badmintonArena.getBadmintonMatchProperties();

        badmintonArena.setState(BadmintonMatchState.STARTING);
        properties.setStarting(true);

        if (properties.getStartingCountdown() == 5) {
            badmintonArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The game will start in 5 seconds, get prepared!");
            badmintonArena.getArenaAnnouncer().sendGlobalSound(Sound.ITEM_GOAT_HORN_SOUND_0, 0.8F, 0.8F);


            badmintonArena.getArenaAnnouncer().sendGlobalTitle("&bGet Ready!", "&7The match is starting!", 0, 60, 40);
        }

        if (properties.getStartingCountdown() == 0) {
            badmintonArena.getArenaAnnouncer().sendGlobalMessage("&7[!] Starting match...");
            badmintonArena.start();

            return;
        }

        badmintonArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
        badmintonArena.getArenaAnnouncer().sendGlobalMessage("&eThe Badminton match will start in &b" + properties.getStartingCountdown() + " second(s)&e.");
        properties.decreaseCountdown();
    }
}
