package com.covercorp.holosports.game.minigame.tug.team;

import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.Optional;

public interface ITugTeamHelper {
    boolean registerTeam(final String identifier,
                         final String display,
                         final String color,
                         final Location spawn);
    void registerTeams();
    boolean unregisterTeam(final String teamIdentifier);
    void unregisterTeams();

    boolean addPlayerToTeam(final ITugPlayer player, String teamIdentifier);
    boolean removePlayerFromTeam(final ITugPlayer player, String teamIdentifier);

    Optional<ITugTeam> getTeam(final String teamIdentifier);

    ImmutableList<ITugTeam> getTeamList();

    Optional<ITugTeam> getTeamWithMostPoints();

    ITugTeam getOppositeTeam(final ITugTeam team);
}
