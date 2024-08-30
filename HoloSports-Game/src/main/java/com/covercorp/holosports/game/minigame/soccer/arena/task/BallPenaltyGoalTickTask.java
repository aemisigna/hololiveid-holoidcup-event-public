package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.ball.stand.BallArmorStand;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.meta.FireworkMeta;

public final class BallPenaltyGoalTickTask implements Runnable {
    private final ISoccerTeamHelper teamHelper;
    private final SoccerArena soccerArena;

    public BallPenaltyGoalTickTask(final SoccerMiniGame soccerMiniGame, final SoccerArena arena) {
        teamHelper = soccerMiniGame.getTeamHelper();
        soccerArena = arena;
    }

    @Override
    public void run() {
        final SoccerBall soccerBall = soccerArena.getSoccerBall();
        if (soccerBall == null) return;

        final ISoccerPlayer ballTagger = soccerArena.getPenaltyKicker();
        if (ballTagger == null) return;

        final ISoccerTeam taggerTeam = ballTagger.getTeam();
        if (taggerTeam == null) return;

        final Cuboid penaltyGoalZone = soccerArena.getPenaltyGoalZoneCuboid();

        final BallArmorStand ballArmorStand = soccerBall.getBallArmorStand();
        if (ballArmorStand == null) return;

        final Slime hitbox = soccerBall.getBallArmorStand().getHitboxStand();
        if (hitbox == null) return;

        final Location hitboxLocation = hitbox.getLocation();

        if (!penaltyGoalZone.containsLocation(hitboxLocation)) return;

        // Penalty scored
        final Firework firework = (Firework) hitboxLocation.getWorld().spawnEntity(hitboxLocation, EntityType.FIREWORK);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        final FireworkEffect effect = FireworkEffect.builder().withColor(Color.YELLOW).withFade(Color.YELLOW).with(Type.BALL_LARGE).build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(3);
        firework.setFireworkMeta(fireworkMeta);

        soccerBall.despawn();

        // The referee scored the penalty
        if (ballTagger.isReferee()) {
            final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
            for (final ISoccerTeam soccerTeam : teamHelper.getTeamList()) {
                teamScoreBuilder
                        .append(ChatColor.valueOf(soccerTeam.getColor()))
                        .append(soccerTeam.getGoals())
                        .append(" &f- ");
            }
            // Remove the last 5 characters (space, dash, space, bracket, space)
            final String score = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] &b" + ballTagger.getName() + " (Referee) &7just scored a penalty! This is not allowed!");
            soccerArena.getArenaAnnouncer().sendGlobalTitle(
                    "&c&lREFEREE IS CRAZY!",
                    score,
                    0,
                    40,
                    0
            );

            Bukkit.getScheduler().runTaskLater(soccerArena.getSoccerMiniGame().getHoloSportsGame(), new BallSpawnTask(soccerArena, soccerBall), 60L);
            return;
        }

        // Score the penalty to the team
        taggerTeam.setPenalties(taggerTeam.getPenalties() + 1);

        final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
        for (final ISoccerTeam soccerTeam : teamHelper.getTeamList()) {
            teamScoreBuilder
                    .append(ChatColor.valueOf(soccerTeam.getColor()))
                    .append(ChatColor.GRAY)
                    .append(soccerTeam.getPenalties())
                    .append("/5")
                    .append(" &f- ");
        }
        // Remove the last 5 characters (space, dash, space, bracket, space)
        final String score = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

        soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] " + ChatColor.valueOf(taggerTeam.getColor()) + ballTagger.getName() + " &7scored a penalty!");
        soccerArena.getArenaAnnouncer().sendGlobalTitle(
                ChatColor.valueOf(taggerTeam.getColor()) + String.valueOf(ChatColor.BOLD) + "PENALTY GOAL!",
                score,
                0,
                40,
                0
        );

        // Check if the team has 5 penalties, if so, they win
        if (taggerTeam.getPenalties() >= 5) {
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] " + ChatColor.valueOf(taggerTeam.getColor()) + taggerTeam.getName() + " &7won the penalty shootout!");
            soccerArena.getArenaAnnouncer().sendGlobalTitle(
                    ChatColor.valueOf(taggerTeam.getColor()) + String.valueOf(ChatColor.BOLD) + "PENALTY SHOOTOUT WON!",
                    score,
                    0,
                    40,
                    0
            );

            taggerTeam.setGoals(taggerTeam.getGoals() + taggerTeam.getPenalties());

            // Add the opponent team's penalties to their goals
            final ISoccerTeam oppositeTeam = teamHelper.getOppositeTeam(taggerTeam);
            if (oppositeTeam != null) {
                oppositeTeam.setGoals(oppositeTeam.getGoals() + oppositeTeam.getPenalties());
            }

            soccerArena.stop();

            return;
        }

        // Rotate penalty and call penalty execute task
        soccerArena.rotatePenaltyKicker();
    }
}
