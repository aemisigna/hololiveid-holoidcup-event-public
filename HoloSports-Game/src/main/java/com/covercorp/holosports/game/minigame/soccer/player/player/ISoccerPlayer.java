package com.covercorp.holosports.game.minigame.soccer.player.player;

import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import java.util.UUID;

public interface ISoccerPlayer {
    UUID getUniqueId();
    String getName();

    ISoccerTeam getTeam();
    void setTeam(final ISoccerTeam team);

    SoccerRole getRole();
    void setRole(final SoccerRole role);

    boolean isReferee();
}
