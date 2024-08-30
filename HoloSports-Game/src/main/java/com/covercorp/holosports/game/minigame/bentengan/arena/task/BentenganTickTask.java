package com.covercorp.holosports.game.minigame.bentengan.arena.task;

import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.listener.event.BentenganMatchTickEvent;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import org.bukkit.Bukkit;

public final class BentenganTickTask implements Runnable {
    private BentenganArena bentenganArena;

    public BentenganTickTask(final BentenganArena bentenganArena) {
        this.bentenganArena = bentenganArena;
    }

    @Override
    public void run() {
        if (bentenganArena.getState() != BentenganMatchState.GAME) return;

        Bukkit.getPluginManager().callEvent(new BentenganMatchTickEvent(bentenganArena));
    }
}
