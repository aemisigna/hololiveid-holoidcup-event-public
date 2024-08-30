package com.covercorp.holosports.game.minigame.bentengan.team.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import org.bukkit.Location;

import java.util.Set;

public interface IBentenganTeam {
    String getIdentifier();
    String getName();

    String getColor();

    Set<IBentenganPlayer> getPlayers();

    Location getSpawnPoint();

    Cuboid getZone();

    Location getJailSpawnPoint();
    Cuboid getJailZone();

    Cuboid getBeaconZone();

    void addPlayer(final IBentenganPlayer player);
    void removePlayer(final IBentenganPlayer player);
}
