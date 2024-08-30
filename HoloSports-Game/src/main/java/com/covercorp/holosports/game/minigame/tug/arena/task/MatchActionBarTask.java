package com.covercorp.holosports.game.minigame.tug.arena.task;

import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import org.bukkit.ChatColor;

public final class MatchActionBarTask implements Runnable {
    private final TugMiniGame tugMiniGame;
    private final TugArena tugArena;

    public MatchActionBarTask(final TugMiniGame tugMiniGame, final TugArena arena) {
        this.tugMiniGame = tugMiniGame;
        this.tugArena = arena;
    }

    @Override
    public void run() {
        if (tugArena.getState() == TugMatchState.WAITING) return;

        final StringBuilder teamScoreBuilder = new StringBuilder("&dScore: &f[");
        // Get all teams, get the team score and color it using the team color and concatenate to the score string
        for (final ITugTeam team : tugArena.getTeamHelper().getTeamList()) {
            teamScoreBuilder
                    .append(ChatColor.valueOf(team.getColor()))
                    .append(team.getPoints())
                    .append(" &f- ");
        }

        // Remove the last 5 characters (space, dash, space, bracket, space)
        final String scoreTunnedBuilder = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

        final String tagger = "&e[&fTime limit: &7%s&e] ";

        // Format the time left to a string
        final String timeLeft = String.format("%02d:%02d", tugArena.getGameTime() / 60, tugArena.getGameTime() % 60);

        tugArena.getArenaAnnouncer().sendGlobalActionBar(
                scoreTunnedBuilder + String.format(tagger, timeLeft)
        );
    }
}
