package com.covercorp.holosports.game.minigame.bentengan.player.player;

import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;

import java.util.UUID;

public interface IBentenganPlayer {
    UUID getUniqueId();
    String getName();

    IBentenganTeam getTeam();
    void setTeam(final IBentenganTeam team);
}
