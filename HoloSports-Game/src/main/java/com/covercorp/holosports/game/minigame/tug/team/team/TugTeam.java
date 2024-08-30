package com.covercorp.holosports.game.minigame.tug.team.team;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
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
public final class TugTeam implements ITugTeam {
    private final String identifier;
    private final String name;
    private final String color;

    private final Set<ITugPlayer> players;

    private final Location spawn;

    @Setter(AccessLevel.PUBLIC) private int points = 0;

    public TugTeam(final String identifier, final String name, final String color, final Location spawn) {
        this.identifier = identifier;
        this.name = name;
        this.color = color;

        this.spawn = spawn;

        players = new HashSet<>();
    }

    @Override
    public void addPlayer(final ITugPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &ejoined team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.add(player);
    }

    @Override
    public void removePlayer(final ITugPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &eleft team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.remove(player);
    }
}
