package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.ChatColor;

public final class MatchActionBarTask implements Runnable {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena soccerArena;

    public MatchActionBarTask(final SoccerMiniGame soccerMiniGame, final SoccerArena arena) {
        this.soccerMiniGame = soccerMiniGame;
        this.soccerArena = arena;
    }

    @Override
    public void run() {
        if (soccerArena.getState() == SoccerMatchState.WAITING) return;

        if (!soccerArena.isPenaltyMode()) {
            final StringBuilder teamScoreBuilder = new StringBuilder("&dScore: &f[");
            // Get all teams, get the team score and color it using the team color and concatenate to the score string
            for (final ISoccerTeam team : soccerMiniGame.getTeamHelper().getTeamList()) {
                teamScoreBuilder
                        .append(ChatColor.valueOf(team.getColor()))
                        .append(team.getGoals())
                        .append(" &f- ");
            }
            // Remove the last 5 characters (space, dash, space, bracket, space)
            final String scoreTunnedBuilder = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

            final String tagger = "&e[&fBall holder: %s&e] ";
            final String time = "&aTime: &f%s";

            final String formattedArenaTime = String.format("%02d:%02d", soccerArena.getGameTime() / 60, soccerArena.getGameTime() % 60);

            final SoccerBall ball = soccerArena.getSoccerBall();
            if (ball == null) return;

            soccerArena.getArenaAnnouncer().sendGlobalActionBar(
                    scoreTunnedBuilder +
                            String.format(tagger, soccerArena.getSoccerBall().getBallTaggerDetail()) +
                            String.format(time, formattedArenaTime)
            );
        } else {
            final StringBuilder teamScoreBuilder = new StringBuilder("&dPenalty Score: &f[");
            // Get all teams, get the team score and color it using the team color and concatenate to the score string
            for (final ISoccerTeam team : soccerMiniGame.getTeamHelper().getTeamList()) {
                teamScoreBuilder
                        .append(ChatColor.valueOf(team.getColor()))
                        .append(team.getGoals())
                        .append(ChatColor.GRAY)
                        .append(" (")
                        .append(team.getPenalties())
                        .append("/5)")
                        .append(" &f- ");
            }
            // Remove the last 5 characters (space, dash, space, bracket, space)
            final String scoreTunnedBuilder = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

            final String tagger = "&e[&fKicking penalty: %s&e] ";

            final SoccerBall ball = soccerArena.getSoccerBall();
            if (ball == null) return;

            final ISoccerPlayer penaltyKicker = soccerArena.getPenaltyKicker();
            if (penaltyKicker == null) return;

            soccerArena.getArenaAnnouncer().sendGlobalActionBar(
                    scoreTunnedBuilder + String.format(tagger, penaltyKicker.getName())
            );
        }
    }
}
