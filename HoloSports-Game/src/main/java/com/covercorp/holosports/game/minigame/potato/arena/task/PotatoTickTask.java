package com.covercorp.holosports.game.minigame.potato.arena.task;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.listener.event.PotatoMatchTickEvent;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import org.bukkit.Bukkit;

public final class PotatoTickTask implements Runnable {
    private PotatoArena potatoArena;

    public PotatoTickTask(final PotatoArena potatoArena) {
        this.potatoArena = potatoArena;
    }

    @Override
    public void run() {
        if (potatoArena.getState() == PotatoMatchState.WAITING) return;

        Bukkit.getPluginManager().callEvent(new PotatoMatchTickEvent(potatoArena));
    }
}
