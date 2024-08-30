package com.covercorp.holosports.game.minigame.tug.arena.task;

import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.properties.TugMatchProperties;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public final class RopeEnableTask implements Runnable {
    final TugArena tugArena;

    public RopeEnableTask(final TugArena arena) {
        this.tugArena = arena;
    }

    @Override
    public void run() {
        final TugMatchProperties properties = tugArena.getTugMatchProperties();

        properties.setRopeStarting(true);

        if (properties.getRopeStartingCountdown() == 10) {
            tugArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The levers will be enabled in 10 seconds, get prepared!");
            tugArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.0F, 2.0F);
        }

        if (properties.getRopeStartingCountdown() <= 5 && properties.getRopeStartingCountdown() > 0) {
            tugArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
            tugArena.getArenaAnnouncer().sendGlobalMessage("&eThe levers will be enabled in &b" + properties.getRopeStartingCountdown() + " second(s)&e.");
        }

        if (properties.getRopeStartingCountdown() == 0) {
            tugArena.enableRopes();
            return;
        }

        properties.decreaseRopeCountdown();
    }
}
