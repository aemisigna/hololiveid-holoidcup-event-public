package com.covercorp.holosports.game.minigame.bentengan.team.team;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

@Getter(AccessLevel.PUBLIC)
public final class BentenganTeam implements IBentenganTeam {
    private final String identifier;
    private final String name;
    private final String color;

    private final Set<IBentenganPlayer> players;

    private final Location spawnPoint;

    private final Cuboid zone;

    private final Location jailSpawnPoint;
    private final Cuboid jailZone;

    private final Cuboid beaconZone;

    public BentenganTeam(
            final String identifier,
            final String name,
            final String color,
            final Location spawnPoint,
            final Cuboid zone,
            final Location jailSpawnPoint,
            final Cuboid jailZone,
            final Cuboid beaconZone) {

        this.identifier = identifier;
        this.name = name;
        this.color = color;

        this.spawnPoint = spawnPoint;

        this.zone = zone;

        this.jailSpawnPoint = jailSpawnPoint;
        this.jailZone = jailZone;
        this.beaconZone = beaconZone;

        players = new HashSet<>();
    }

    @Override
    public void addPlayer(final IBentenganPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &ejoined team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.add(player);
    }

    @Override
    public void removePlayer(final IBentenganPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &eleft team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.remove(player);
    }
}
