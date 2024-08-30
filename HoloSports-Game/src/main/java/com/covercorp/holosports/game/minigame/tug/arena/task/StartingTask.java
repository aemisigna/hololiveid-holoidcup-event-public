package com.covercorp.holosports.game.minigame.tug.arena.task;

import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.properties.TugMatchProperties;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import org.bukkit.Sound;

public final class StartingTask implements Runnable {
    final TugArena tugArena;

    public StartingTask(final TugArena arena) {
        this.tugArena = arena;
    }

    @Override
    public void run() {
        final TugMatchProperties properties = tugArena.getTugMatchProperties();

        tugArena.setState(TugMatchState.STARTING);

        properties.setStarting(true);

        if (properties.getStartingCountdown() == 5) {
            tugArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The game will start in 5 seconds, get prepared!");
            tugArena.getArenaAnnouncer().sendGlobalSound(Sound.ITEM_GOAT_HORN_SOUND_0, 0.8F, 0.8F);

            tugArena.getArenaAnnouncer().sendGlobalTitle("&bGet Ready!", "&7The match is starting!", 0, 60, 40);
        }

        if (properties.getStartingCountdown() == 0) {
            tugArena.getArenaAnnouncer().sendGlobalMessage("&7[!] Starting match...");

            tugArena.start();

            return;
        }

        tugArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
        tugArena.getArenaAnnouncer().sendGlobalMessage("&eThe Tug of War match will start in &b" + properties.getStartingCountdown() + " second(s)&e.");
        properties.decreaseCountdown();
    }
}
