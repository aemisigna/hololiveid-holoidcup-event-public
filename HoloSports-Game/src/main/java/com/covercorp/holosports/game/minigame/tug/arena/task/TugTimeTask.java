package com.covercorp.holosports.game.minigame.tug.arena.task;

import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;

public final class TugTimeTask implements Runnable {
    private final TugArena tugArena;

    public TugTimeTask(final TugArena tugArena) {
        this.tugArena = tugArena;
    }

    @Override
    public void run() {
        if (tugArena.getState() != TugMatchState.GAME) return;

        tugArena.setGameTime(tugArena.getGameTime() - 1);

        if (tugArena.getGameTime() <= 0) {
            tugArena.runLoser();
        }
    }
}
