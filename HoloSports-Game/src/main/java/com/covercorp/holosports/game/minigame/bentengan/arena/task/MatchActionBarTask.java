package com.covercorp.holosports.game.minigame.bentengan.arena.task;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public final class MatchActionBarTask implements Runnable {
    private final BentenganMiniGame bentenganMiniGame;
    private final BentenganArena bentenganArena;

    public MatchActionBarTask(final BentenganMiniGame bentenganMiniGame, final BentenganArena arena) {
        this.bentenganMiniGame = bentenganMiniGame;
        this.bentenganArena = arena;
    }

    @Override
    public void run() {
        if (bentenganArena.getState() == BentenganMatchState.WAITING) return;

        final StringBuilder teamScoreBuilder = new StringBuilder("&f[&d⛓ ");
        for (final IBentenganTeam team : bentenganArena.getTeamHelper().getTeamList()) {
            int jailedTalents = 0;
            for (final IBentenganPlayer player : team.getPlayers()) {
                if (bentenganArena.isJailed(player)) jailedTalents++;
            }

            teamScoreBuilder
                    .append(ChatColor.valueOf(team.getColor()))
                    .append(jailedTalents)
                    .append(" &f- ");
        }

        // Remove the last 5 characters (space, dash, space, bracket, space)
        final String scoreTunnedBuilder = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

        final String timeLimit = "&f[⌚: &7%s&f] ";
        // Format the time left to a string
        final String timeLeft = String.format("%02d:%02d", bentenganArena.getTimeLimit() / 60, bentenganArena.getTimeLimit() % 60);

        bentenganArena.getPlayerHelper().getPlayerList().forEach(participant -> {
            final IBentenganTeam team = participant.getTeam();
            if (team == null) return;

            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            final String hpInfo = "&f[&c%s ❤ &f| &b☀ %s&f] ";

            final double hp = player.getHealth();
            final double hunger = player.getFoodLevel();
            final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(scoreTunnedBuilder + String.format(hpInfo, decimalFormat.format(hp), decimalFormat.format(hunger)) + String.format(timeLimit, timeLeft))));
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (bentenganArena.getPlayerHelper().getPlayer(player.getUniqueId()).isPresent()) return;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(scoreTunnedBuilder + String.format(timeLimit, timeLeft))));
        });
    }
}
