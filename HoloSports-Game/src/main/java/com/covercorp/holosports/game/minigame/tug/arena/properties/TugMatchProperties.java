package com.covercorp.holosports.game.minigame.tug.arena.properties;

import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class TugMatchProperties {
    private int startingTime;
    private int startingCountdown;

    private boolean starting;

    private int ropeStartingTime;
    private int ropeStartingCountdown;
    private boolean ropeStarting;

    private int startingTaskId;
    private int ropeStartingTaskId;

    public TugMatchProperties(final TugArena tugArena) {
        startingTime = 5;
        startingCountdown = startingTime;

        ropeStartingTime = 10;
        ropeStartingCountdown = ropeStartingTime;

        starting = false;
    }

    public void decreaseCountdown() {
        startingCountdown--;
    }

    public void decreaseRopeCountdown() {
        ropeStartingCountdown--;
    }

    public void resetTimer() {
        setStartingTime(5);
        setStarting(false);
        setStartingTaskId(0);
        setStartingCountdown(startingTime);

        setRopeStartingTime(10);
        setRopeStarting(false);
        setRopeStartingTaskId(0);
        setRopeStartingCountdown(ropeStartingTime);
    }
}
