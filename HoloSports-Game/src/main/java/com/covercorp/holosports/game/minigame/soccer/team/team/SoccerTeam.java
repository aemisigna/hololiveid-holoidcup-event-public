package com.covercorp.holosports.game.minigame.soccer.team.team;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

@Getter(AccessLevel.PUBLIC)
public final class SoccerTeam implements ISoccerTeam {
    private final String identifier;
    private final String name;
    private final String color;

    private final Set<ISoccerPlayer> players;

    private final Queue<ISoccerPlayer> penaltyRotationQueue;

    private final Location goalKeeperSpawn;
    private final List<Location> standardSpawns;

    private final Cuboid goalCuboid;

    private final Cuboid goalSafeCuboid;

    @Setter(AccessLevel.PUBLIC) private int goals = 0;

    @Setter(AccessLevel.PUBLIC) private int penalties = 0;

    public SoccerTeam(
            final String identifier,
            final String name,
            final String color,
            final Location goalKeeperSpawn,
            final List<Location> standardSpawns,
            final Cuboid goalCuboid,
            final Cuboid goalSafeCuboid) {

        this.identifier = identifier;
        this.name = name;
        this.color = color;
        this.goalKeeperSpawn = goalKeeperSpawn;
        this.standardSpawns = standardSpawns;
        this.goalCuboid = goalCuboid;
        this.goalSafeCuboid = goalSafeCuboid;

        players = new HashSet<>();
        penaltyRotationQueue = new LinkedList<>();
    }

    @Override
    public void addPlayer(final ISoccerPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &ejoined team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.add(player);
    }

    @Override
    public void removePlayer(final ISoccerPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &eleft team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.remove(player);
    }

    public List<ISoccerPlayer> getGoalkeepers() {
        return getPlayers().stream().filter(player -> player.getRole() == SoccerRole.GOALKEEPER).toList();
    }

    public List<ISoccerPlayer> getStandards() {
        return getPlayers().stream().filter(player -> player.getRole() == SoccerRole.STANDARD).toList();
    }
}
