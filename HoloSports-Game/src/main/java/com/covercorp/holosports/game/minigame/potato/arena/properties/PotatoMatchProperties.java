package com.covercorp.holosports.game.minigame.potato.arena.properties;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class PotatoMatchProperties {
    private int startingTime;
    private int startingCountdown;

    private boolean starting;

    private int raceStartingTime;
    private int raceStartingCountdown;
    private boolean raceStarting;

    private int startingTaskId;
    private int raceStartingTaskId;

    public PotatoMatchProperties(final PotatoArena potatoArena) {
        startingTime = 5;
        startingCountdown = startingTime;

        raceStartingTime = 10;
        raceStartingCountdown = raceStartingTime;

        starting = false;
    }

    public void decreaseCountdown() {
        startingCountdown--;
    }

    public void decreaseRaceCountdown() {
        raceStartingCountdown--;
    }

    public void resetTimer() {
        setStartingTime(5);
        setStarting(false);
        setStartingTaskId(0);
        setStartingCountdown(startingTime);

        setRaceStartingTime(10);
        setRaceStarting(false);
        setRaceStartingTaskId(0);
        setRaceStartingCountdown(raceStartingTime);
    }
}
