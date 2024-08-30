package com.covercorp.holosports.game.minigame.potato.team.team;

import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;

import java.util.Set;

public interface IPotatoTeam {
    String getIdentifier();
    String getName();

    String getColor();

    boolean reachedGoal();

    Set<IPotatoPlayer> getPlayers();

    void addPlayer(final IPotatoPlayer player);
    void removePlayer(final IPotatoPlayer player);

    int getFinishedParticipants();
}
