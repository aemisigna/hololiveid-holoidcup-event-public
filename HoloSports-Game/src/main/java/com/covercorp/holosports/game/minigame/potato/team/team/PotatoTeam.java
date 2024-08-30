package com.covercorp.holosports.game.minigame.potato.team.team;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

@Getter(AccessLevel.PUBLIC)
public final class PotatoTeam implements IPotatoTeam {
    private final String identifier;
    private final String name;
    private final String color;

    private final Set<IPotatoPlayer> players;

    public PotatoTeam(final String identifier, final String name, final String color) {
        this.identifier = identifier;
        this.name = name;
        this.color = color;

        players = new HashSet<>();
    }

    @Override
    public boolean reachedGoal() {
        // Loop through all players in the team, if they touched half and goal, return true
        boolean finished;
        for (final IPotatoPlayer player : players) {
            finished = player.isTouchedHalf() && player.isTouchedGoal();
            if (!finished) return false;
        }

        return true;
    }

    @Override
    public void addPlayer(final IPotatoPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &ejoined team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.add(player);
    }

    @Override
    public void removePlayer(final IPotatoPlayer player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.sendMessage(CommonUtil.colorize("&b" + player.getName() + " &eleft team " + ChatColor.valueOf(getColor()) + getName() + "&e."))
        );
        players.remove(player);
    }

    @Override
    public int getFinishedParticipants() {
        int finished = 0;
        for (final IPotatoPlayer player : players) {
            if (player.isFinishedRace()) finished++;
        }
        return finished;
    }
}
