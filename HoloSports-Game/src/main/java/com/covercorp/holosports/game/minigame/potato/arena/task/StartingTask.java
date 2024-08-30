package com.covercorp.holosports.game.minigame.potato.arena.task;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.properties.PotatoMatchProperties;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import org.bukkit.Sound;

public final class StartingTask implements Runnable {
    final PotatoArena potatoArena;

    public StartingTask(final PotatoArena arena) {
        this.potatoArena = arena;
    }

    @Override
    public void run() {
        final PotatoMatchProperties properties = potatoArena.getPotatoMatchProperties();

        potatoArena.setState(PotatoMatchState.STARTING);

        properties.setStarting(true);

        if (properties.getStartingCountdown() == 5) {
            potatoArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The game will start in 5 seconds, get prepared!");
            potatoArena.getArenaAnnouncer().sendGlobalSound(Sound.ITEM_GOAT_HORN_SOUND_0, 0.8F, 0.8F);

            potatoArena.getArenaAnnouncer().sendGlobalTitle("&bGet Ready!", "&7The match is starting!", 0, 60, 40);
        }

        if (properties.getStartingCountdown() == 0) {
            potatoArena.getArenaAnnouncer().sendGlobalMessage("&7[!] Starting match...");

            potatoArena.start();

            return;
        }

        potatoArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
        potatoArena.getArenaAnnouncer().sendGlobalMessage("&eThe Potato Sack Race match will start in &b" + properties.getStartingCountdown() + " second(s)&e.");
        properties.decreaseCountdown();
    }
}
