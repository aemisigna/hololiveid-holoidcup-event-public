package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;

/**
 * This class is a runnable that will be executed after the penalty shooter is selected.
 * The shooter selection logic should be the following:
 * 1. If there's no penalties shot yet, select a random team to shoot.
 * 2. Using the shooter team, get the opposing team and teleport their goalkeeper to the goal.
 * 3. Make all the remaining players invisible using @see Player#setGameMode(GameMode#SPECTATOR)
 * <p>
 * The runnable should do the following:
 * 1. Teleport the goalkeeper to the Penalty Goalkeeper Location.
 * 2. Teleport the shooter to the Penalty Shooter Location.
 * 3. Check both players to be visible and in @see Player#setGameMode(GameMode#SURVIVAL)
 * 4. Set mandatory tagger to the shooter.
 * 5. Spawn the ball calling the BallSpawnTask after 2 seconds.
 */
public final class BallExecutePenaltyTask implements Runnable {
    private final ISoccerTeamHelper teamHelper;
    private final SoccerArena soccerArena;

    public BallExecutePenaltyTask(final SoccerMiniGame soccerMiniGame, final SoccerArena arena) {
        teamHelper = soccerMiniGame.getTeamHelper();
        soccerArena = arena;
    }

    @Override
    public void run() {
        // Get the penalty shooter
        final ISoccerPlayer penaltyShooter = soccerArena.getPenaltyKicker();
        if (penaltyShooter == null) return;

        // Get the shooter team
        final ISoccerTeam shooterTeam = penaltyShooter.getTeam();
        if (shooterTeam == null) return;

        final ISoccerTeam opposingTeam = teamHelper.getOppositeTeam(shooterTeam);
        if (opposingTeam == null) return;

        final ISoccerPlayer goalkeeper = opposingTeam.getGoalkeepers().iterator().next();
        if (goalkeeper == null) return;

        // Now we have our subjects, let's make the checks
        if (goalkeeper.getRole() != SoccerRole.GOALKEEPER) return;
        if (penaltyShooter.getRole() != SoccerRole.STANDARD) return;

        // Instance the Player objects
        final Player goalkeeperPlayer = Bukkit.getPlayer(goalkeeper.getUniqueId());
        if (goalkeeperPlayer == null) return;
        final Player shooterPlayer = Bukkit.getPlayer(penaltyShooter.getUniqueId());
        if (shooterPlayer == null) return;

        goalkeeperPlayer.setGameMode(GameMode.ADVENTURE);
        shooterPlayer.setGameMode(GameMode.ADVENTURE);

        // Teleport the goalkeeper to the goal
        goalkeeperPlayer.teleport(soccerArena.getPenaltyGoalkeeperSpawnLocation());
        shooterPlayer.teleport(soccerArena.getPenaltyShooterSpawnLocation());

        final SoccerBall soccerBall = soccerArena.getSoccerBall();
        if (soccerBall == null) {
            return;
        }

        // Set the shooter as the mandatory tagger
        soccerBall.setMandatoryTagger(penaltyShooter);

        // Call ball spawn task 2 seconds later
        Bukkit.getScheduler().runTaskLater(
                soccerArena.getSoccerMiniGame().getHoloSportsGame(),
                new BallSpawnTask(soccerArena, soccerArena.getSoccerBall(), soccerArena.getPenaltyBallSpawnLocation()),
                20L * 2L);
    }
}