package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.BadmintonBall;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.bukkit.ChatColor;

public final class MatchActionBarTask implements Runnable {
    private final BadmintonMiniGame badmintonMiniGame;
    private final BadmintonArena badmintonArena;

    public MatchActionBarTask(final BadmintonMiniGame badmintonMiniGame, final BadmintonArena arena) {
        this.badmintonMiniGame = badmintonMiniGame;
        this.badmintonArena = arena;
    }

    @Override
    public void run() {
        if (badmintonArena.getState() == BadmintonMatchState.WAITING) return;

        final StringBuilder teamScoreBuilder = new StringBuilder("&dScore: &f[");
        // Get all teams, get the team score and color it using the team color and concatenate to the score string
        for (final IBadmintonTeam team : badmintonArena.getTeamHelper().getTeamList()) {
            teamScoreBuilder
                    .append(ChatColor.valueOf(team.getColor()))
                    .append(team.getPoints())
                    .append(" &f- ");
        }
        // Remove the last 5 characters (space, dash, space, bracket, space)
        final String scoreTunnedBuilder = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

        final String tagger = "&e[&fShuttle tagger: %s&e] ";

        final BadmintonBall ball = badmintonArena.getBadmintonBall();
        if (ball == null) return;

        badmintonArena.getArenaAnnouncer().sendGlobalActionBar(
                scoreTunnedBuilder +
                        String.format(tagger, badmintonArena.getBadmintonBall().getBallTaggerDetail())
        );
    }
}
