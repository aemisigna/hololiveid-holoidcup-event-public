package com.covercorp.holosports.game.minigame.badminton.team.team;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter(AccessLevel.PUBLIC)
public final class BadmintonTeam implements IBadmintonTeam {
    private final String identifier;
    private final String name;
    private final String color;

    private final Set<IBadmintonPlayer> players;

    private final Cuboid singlePointZone;
    private final Cuboid doublePointZone;
    private final Cuboid singleServePointZoneEven;
    private final Cuboid singleServePointZoneOdd;
    private final Cuboid doubleServePointZoneEven;
    private final Cuboid doubleServePointZoneOdd;

    private final Location singleServePositionEven;
    private final Location singleServePositionOdd;
    private final Location doubleServePositionEven;
    private final Location doubleServePositionOdd;

    private final Location shuttlecockSpawnEven;
    private final Location shuttlecockSpawnOdd;
    private final Location shuttlecockSpawnNormal;

    private final List<Location> standardSpawns;

    @Setter(AccessLevel.PUBLIC) private int points = 0;

    public BadmintonTeam(
            final String identifier,
            final String name,
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
            final List<Location> standardSpawns) {
        this.identifier = identifier;
        this.name = name;
        this.color = color;
        this.singlePointZone = singlePointZone;
        this.doublePointZone = doublePointZone;
        this.singleServePointZoneEven = singleServePointZoneEven;
        this.singleServePointZoneOdd = singleServePointZoneOdd;
        this.doubleServePointZoneEven = doubleServePointZoneEven;
        this.doubleServePointZoneOdd = doubleServePointZoneOdd;
        this.singleServePositionEven = singleServePositionEven;
        this.singleServePositionOdd = singleServePositionOdd;
        this.doubleServePositionEven = doubleServePositionEven;
        this.doubleServePositionOdd = doubleServePositionOdd;
        this.shuttlecockSpawnEven = shuttlecockSpawnEven;
        this.shuttlecockSpawnOdd = shuttlecockSpawnOdd;
        this.shuttlecockSpawnNormal = shuttlecockSpawnNormal;

        this.standardSpawns = standardSpawns;

        players = new HashSet<>();
    }

    @Override
    public void addPlayer(final IBadmintonPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &ejoined team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.add(player);
    }

    @Override
    public void removePlayer(final IBadmintonPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &eleft team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.remove(player);
    }

    public List<IBadmintonPlayer> getStandards() {
        return getPlayers().stream().toList();
    }
}
