package com.covercorp.holosports.game.minigame.soccer.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public interface ISoccerTeamHelper {
    boolean registerTeam(final String identifier, final String display, final String color, final Location goalKeeperSpawn, final List<Location> standardSpawns, final Cuboid goalCuboid, final Cuboid goalSafeCuboid);
    void registerTeams();
    boolean unregisterTeam(final String teamIdentifier);
    void unregisterTeams();

    boolean addPlayerToTeam(final ISoccerPlayer player, String teamIdentifier);
    boolean removePlayerFromTeam(final ISoccerPlayer player, String teamIdentifier);

    Optional<ISoccerTeam> getTeam(final String teamIdentifier);

    ImmutableList<ISoccerTeam> getTeamList();

    Optional<ISoccerTeam> getTeamWithMostGoals();

    ISoccerTeam getOppositeTeam(final ISoccerTeam team);

    List<Cuboid> getGoalCuboids();
}
