package com.covercorp.holosports.game.minigame.badminton.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public interface IBadmintonTeamHelper {
    boolean registerTeam(final String identifier,
                         final String display,
                         final String color,
                         final Cuboid singlePointZone,
                         final Cuboid doublePointZone,
                         final Cuboid singleServePointZoneEven,
                         final Cuboid singleServePointZoneOdd,
                         final Cuboid doubleServePointZoneEven,
                         final Cuboid doubleServePointZoneOdd,
                         final Location singleServePositionEven,
                         final Location singleServePositionOdd,
                         final Location doubleServePositionEven,
                         final Location doubleServePositionOdd,
                         final Location shuttlecockSpawnEven,
                         final Location shuttlecockSpawnOdd,
                         final Location shuttlecockSpawnNormal,
                         final List<Location> standardSpawns);
    void registerTeams();
    boolean unregisterTeam(final String teamIdentifier);
    void unregisterTeams();

    boolean addPlayerToTeam(final IBadmintonPlayer player, String teamIdentifier);
    boolean removePlayerFromTeam(final IBadmintonPlayer player, String teamIdentifier);

    Optional<IBadmintonTeam> getTeam(final String teamIdentifier);

    ImmutableList<IBadmintonTeam> getTeamList();

    Optional<IBadmintonTeam> getTeamWithMostPoints();

    IBadmintonTeam getOppositeTeam(final IBadmintonTeam team);
}
