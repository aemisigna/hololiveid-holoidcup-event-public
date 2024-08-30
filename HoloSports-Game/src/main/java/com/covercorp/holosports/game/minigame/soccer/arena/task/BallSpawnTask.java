package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import org.bukkit.Location;

public final class BallSpawnTask implements Runnable {
    private final SoccerArena soccerArena;
    private final SoccerBall soccerBall;

    private final Location spawnLocation;

    public BallSpawnTask(final SoccerArena arena, final SoccerBall soccerBall) {
        this.soccerArena = arena;
        this.soccerBall = soccerBall;

        spawnLocation = arena.getBallSpawnLocation();
    }


    public BallSpawnTask(final SoccerArena arena, final SoccerBall soccerBall, final Location location) {
        this.soccerArena = arena;
        this.soccerBall = soccerBall;

        spawnLocation = location;
    }

    @Override
    public void run() {
        if (soccerArena.getState() != SoccerMatchState.GAME) return;

        soccerBall.spawn(spawnLocation);
    }
}