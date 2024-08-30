package com.covercorp.holosports.game.minigame.tug.player.player;

import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;

import java.util.UUID;

public interface ITugPlayer {
    UUID getUniqueId();
    String getName();

    boolean isSpectating();

    ITugTeam getTeam();
    void setTeam(final ITugTeam team);
}
