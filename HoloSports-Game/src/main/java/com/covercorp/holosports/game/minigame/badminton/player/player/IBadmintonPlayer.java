package com.covercorp.holosports.game.minigame.badminton.player.player;

import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;

import java.util.UUID;

public interface IBadmintonPlayer {
    UUID getUniqueId();
    String getName();

    IBadmintonTeam getTeam();
    void setTeam(final IBadmintonTeam team);
}
