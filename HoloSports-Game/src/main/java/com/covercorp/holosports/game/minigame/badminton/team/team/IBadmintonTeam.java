package com.covercorp.holosports.game.minigame.badminton.team.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import org.bukkit.Location;

import java.util.List;
import java.util.Set;

public interface IBadmintonTeam {
    String getIdentifier();
    String getName();

    String getColor();

    int getPoints();
    void setPoints(int goals);

    List<Location> getStandardSpawns();
    Cuboid getSinglePointZone();
    Cuboid getDoublePointZone();
    Cuboid getSingleServePointZoneEven();
    Cuboid getSingleServePointZoneOdd();
    Cuboid getDoubleServePointZoneEven();
    Cuboid getDoubleServePointZoneOdd();

    Location getSingleServePositionEven();
    Location getSingleServePositionOdd();
    Location getDoubleServePositionEven();
    Location getDoubleServePositionOdd();

    Location getShuttlecockSpawnEven();
    Location getShuttlecockSpawnOdd();
    Location getShuttlecockSpawnNormal();

    Set<IBadmintonPlayer> getPlayers();

    void addPlayer(final IBadmintonPlayer player);
    void removePlayer(final IBadmintonPlayer player);

    List<IBadmintonPlayer> getStandards();
}
