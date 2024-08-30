package com.covercorp.holosports.game.minigame.bentengan.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.Optional;

public interface IBentenganTeamHelper {
    boolean registerTeam(final String identifier, final String display, final String color, final Location spawnPoint, final Cuboid zone, final Location jailSpawnPoint, final Cuboid jailZone, final Cuboid beaconZone);
    void registerTeams();
    boolean unregisterTeam(final String teamIdentifier);
    void unregisterTeams();

    boolean addPlayerToTeam(final IBentenganPlayer player, String teamIdentifier);
    boolean removePlayerFromTeam(final IBentenganPlayer player, String teamIdentifier);

    Optional<IBentenganTeam> getTeam(final String teamIdentifier);

    ImmutableList<IBentenganTeam> getTeamList();

    IBentenganTeam getOppositeTeam(final IBentenganTeam team);
}
