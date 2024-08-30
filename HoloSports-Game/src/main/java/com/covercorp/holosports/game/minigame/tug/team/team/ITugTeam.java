package com.covercorp.holosports.game.minigame.tug.team.team;

import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;

import org.bukkit.Location;

import java.util.Set;

public interface ITugTeam {
    String getIdentifier();
    String getName();

    String getColor();

    int getPoints();
    void setPoints(int goals);

    Location getSpawn();

    Set<ITugPlayer> getPlayers();

    void addPlayer(final ITugPlayer player);
    void removePlayer(final ITugPlayer player);
}
