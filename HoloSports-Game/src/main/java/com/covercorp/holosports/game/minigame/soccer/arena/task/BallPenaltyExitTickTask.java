package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.Location;

public final class BallPenaltyExitTickTask implements Runnable {
    private final ISoccerPlayerHelper playerHelper;
    private final ISoccerTeamHelper teamHelper;
    private final SoccerArena soccerArena;

    public BallPenaltyExitTickTask(final SoccerArena arena) {
        playerHelper = arena.getSoccerMiniGame().getPlayerHelper();
        teamHelper = arena.getSoccerMiniGame().getTeamHelper();
        soccerArena = arena;
    }

    @Override
    public void run() {
        final SoccerBall soccerBall = soccerArena.getSoccerBall();
        if (soccerBall == null) return;

        final ISoccerPlayer ballTagger = soccerBall.getBallTagger();
        if (ballTagger == null) return;

        final ISoccerTeam taggerTeam = ballTagger.getTeam();
        if (taggerTeam == null) return;

        final Location lastBallLocation = soccerBall.getBallArmorStand().getHitboxStand().getLocation();

        if (soccerArena.getPenaltyPlayZoneCuboid().containsLocation(lastBallLocation)) return;

        // I'll use this to know if the ball is inside the penalty goal cuboid
        boolean flagged = soccerArena.getPenaltyGoalZoneCuboid().containsLocation(lastBallLocation);

        if (flagged) return;

        soccerBall.despawn();

        // The ball went out, the kicker missed the penalty
        soccerArena.getArenaAnnouncer().sendGlobalMessage("&c[!] &b" + ballTagger.getName() + " &cmissed the penalty!");
        soccerArena.getArenaAnnouncer().sendGlobalTitle(
                "&c&lPENALTY MISSED!",
                "&f" + ballTagger.getName() + " &7missed the penalty!",
                0, 50, 0
        );

        soccerArena.rotatePenaltyKicker();
    }
}
