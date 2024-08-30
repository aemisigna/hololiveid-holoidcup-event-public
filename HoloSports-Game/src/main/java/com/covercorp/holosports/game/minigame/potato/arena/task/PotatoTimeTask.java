package com.covercorp.holosports.game.minigame.potato.arena.task;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;

public final class PotatoTimeTask implements Runnable {
    private final PotatoArena potatoArena;

    public PotatoTimeTask(final PotatoArena potatoArena) {
        this.potatoArena = potatoArena;
    }

    @Override
    public void run() {
        if (potatoArena.getState() != PotatoMatchState.GAME) return;

        potatoArena.setGameTime(potatoArena.getGameTime() + 1);
    }
}
