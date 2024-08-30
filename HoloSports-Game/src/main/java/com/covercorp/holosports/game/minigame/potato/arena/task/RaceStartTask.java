package com.covercorp.holosports.game.minigame.potato.arena.task;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.properties.PotatoMatchProperties;
import org.bukkit.Sound;

public final class RaceStartTask implements Runnable {
    final PotatoArena potatoArena;

    public RaceStartTask(final PotatoArena arena) {
        this.potatoArena = arena;
    }

    @Override
    public void run() {
        final PotatoMatchProperties properties = potatoArena.getPotatoMatchProperties();

        properties.setRaceStarting(true);

        if (properties.getRaceStartingCountdown() == 10) {
            potatoArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The race will start in 10 seconds, get prepared!");
            potatoArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.0F, 2.0F);
        }

        if (properties.getRaceStartingCountdown() <= 5 && properties.getRaceStartingCountdown() > 0) {
            potatoArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
            potatoArena.getArenaAnnouncer().sendGlobalMessage("&eThe race will start in &b" + properties.getRaceStartingCountdown() + " second(s)&e.");
        }

        if (properties.getRaceStartingCountdown() == 0) {
            potatoArena.startRace();
            return;
        }

        properties.decreaseRaceCountdown();
    }
}
