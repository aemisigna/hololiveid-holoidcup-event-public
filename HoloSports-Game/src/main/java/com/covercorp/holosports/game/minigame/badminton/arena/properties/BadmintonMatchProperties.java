package com.covercorp.holosports.game.minigame.badminton.arena.properties;

import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class BadmintonMatchProperties {
    private int startingTime;
    private int startingCountdown;

    private boolean starting;

    private int startingTaskId;

    private boolean particleMode;
    private int floorToleranceTime;

    public BadmintonMatchProperties(final BadmintonArena badmintonArena) {
        startingTime = 5;
        startingCountdown = startingTime;

        starting = false;

        particleMode = false;
        floorToleranceTime = 12;
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
