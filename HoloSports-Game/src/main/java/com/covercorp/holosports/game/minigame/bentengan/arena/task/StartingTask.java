package com.covercorp.holosports.game.minigame.bentengan.arena.task;

import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.properties.BentenganMatchProperties;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import org.bukkit.Sound;

public final class StartingTask implements Runnable {
    final BentenganArena bentenganArena;

    public StartingTask(final BentenganArena arena) {
        this.bentenganArena = arena;
    }

    @Override
    public void run() {
        final BentenganMatchProperties properties = bentenganArena.getBentenganMatchProperties();

        bentenganArena.setState(BentenganMatchState.STARTING);
        properties.setStarting(true);

        if (properties.getStartingCountdown() == 5) {
            bentenganArena.getArenaAnnouncer().sendGlobalMessage("&7[!] The game will start in 5 seconds, get prepared!");
            bentenganArena.getArenaAnnouncer().sendGlobalSound(Sound.ITEM_GOAT_HORN_SOUND_0, 0.8F, 0.8F);


            bentenganArena.getArenaAnnouncer().sendGlobalTitle("&bGet Ready!", "&7The match is starting!", 0, 60, 40);
        }

        if (properties.getStartingCountdown() == 0) {
            bentenganArena.getArenaAnnouncer().sendGlobalMessage("&7[!] Starting match...");
            bentenganArena.start();

            return;
        }

        bentenganArena.getArenaAnnouncer().sendGlobalSound(Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
        bentenganArena.getArenaAnnouncer().sendGlobalMessage("&eThe Bentengan match will start in &b" + properties.getStartingCountdown() + " second(s)&e.");
        properties.decreaseCountdown();
    }
}
