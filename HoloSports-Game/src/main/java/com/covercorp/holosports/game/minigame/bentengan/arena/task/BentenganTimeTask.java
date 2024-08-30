package com.covercorp.holosports.game.minigame.bentengan.arena.task;

import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;

public final class BentenganTimeTask implements Runnable {
    private final BentenganArena bentenganArena;

    public BentenganTimeTask(final BentenganArena bentenganArena) {
        this.bentenganArena = bentenganArena;
    }

    @Override
    public void run() {
        if (bentenganArena.getState() != BentenganMatchState.GAME) return;

        bentenganArena.setTimeLimit(bentenganArena.getTimeLimit() - 1);

        // Countdown the last 5 seconds
        if (bentenganArena.getTimeLimit() <= 5 && bentenganArena.getTimeLimit() > 0) {
            bentenganArena.getArenaAnnouncer().sendGlobalMessage("&eThe Bentengan match will end in &b" + bentenganArena.getTimeLimit() + " &eseconds!");
        }

        if (bentenganArena.getTimeLimit() <= 0) {
            bentenganArena.setWinnerTeam(null);
            bentenganArena.stop();
        }
    }
}
