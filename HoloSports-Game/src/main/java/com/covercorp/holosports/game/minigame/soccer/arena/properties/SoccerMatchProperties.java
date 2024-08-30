package com.covercorp.holosports.game.minigame.soccer.arena.properties;

import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class SoccerMatchProperties {
    private int startingTime;
    private int startingCountdown;

    private boolean starting;

    private int startingTaskId;

    public SoccerMatchProperties(final SoccerArena soccerArena) {
        startingTime = 5;
        startingCountdown = startingTime;

        starting = false;
    }

    public void decreaseCountdown() {
        startingCountdown--;
    }

    public void resetTimer() {
        setStartingTime(5);
        setStarting(false);
        setStartingTaskId(0);
        setStartingCountdown(startingTime);
    }
}
