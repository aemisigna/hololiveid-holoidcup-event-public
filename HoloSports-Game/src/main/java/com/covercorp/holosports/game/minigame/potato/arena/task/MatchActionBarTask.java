package com.covercorp.holosports.game.minigame.potato.arena.task;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MatchActionBarTask implements Runnable {
    private final PotatoMiniGame potatoMiniGame;
    private final PotatoArena potatoArena;

    public MatchActionBarTask(final PotatoMiniGame potatoMiniGame, final PotatoArena arena) {
        this.potatoMiniGame = potatoMiniGame;
        this.potatoArena = arena;
    }

    @Override
    public void run() {
        if (potatoArena.getState() == PotatoMatchState.WAITING) return;

        final String time = "&e[&fGame time: &7%s&e] ";
        final String timeLeft = String.format("%02d:%02d", potatoArena.getGameTime() / 60, potatoArena.getGameTime() % 60);

        final String laps = "&e[&fLaps: &7%s/%s&e] ";
        final String finished = "&e[&fFinished race: &7%s/%s&e]";

        potatoArena.getPlayerHelper().getPlayerList().forEach(participant -> {
            final IPotatoTeam team = participant.getTeam();
            if (team == null) return;

            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            // Format the time left to a string
            final String lapsInfo = String.format(laps, participant.getFinishedLaps(), potatoArena.getRaceLaps());

            final int finishedRaceParticipants = potatoArena.getPlayerHelper().getPlayerList().stream().filter(IPotatoPlayer::isFinishedRace).toList().size();
            final String finishedInfo = String.format(finished, finishedRaceParticipants, team.getPlayers().size());

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(String.format(time, timeLeft) + lapsInfo + finishedInfo)));
        });

        final StringBuilder teamScoreBuilder = new StringBuilder("&dFinished score: &f[");
        // Get all teams, get the team score and color it using the team color and concatenate to the score string
        for (final IPotatoTeam team : potatoArena.getTeamHelper().getTeamList()) {
            teamScoreBuilder
                    .append(ChatColor.valueOf(team.getColor()))
                    .append(team.getName())
                    .append(ChatColor.GRAY)
                    .append(" (")
                    .append(team.getFinishedParticipants())
                    .append("/")
                    .append(team.getPlayers().size())
                    .append(")")
                    .append(" &e- ");
        }
        // Remove the last 5 characters (space, dash, space, bracket, space)
        final String scoreTunnedBuilder = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

        // Get the players that are not participating
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (potatoArena.getPlayerHelper().getPlayer(player.getUniqueId()).isPresent()) return;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(String.format(time, timeLeft) + scoreTunnedBuilder)));
        });
    }
}
