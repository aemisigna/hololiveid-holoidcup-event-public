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
import org.bukkit.entity.Slime;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public final class BallGoalTickTask implements Runnable {
    private final ISoccerTeamHelper teamHelper;
    private final SoccerArena soccerArena;

    public BallGoalTickTask(final SoccerMiniGame soccerMiniGame, final SoccerArena arena) {
        teamHelper = soccerMiniGame.getTeamHelper();
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

        teamHelper.getTeamList().forEach(team -> {
            final Cuboid goalCuboid = team.getGoalCuboid();

            final BallArmorStand ballArmorStand = soccerBall.getBallArmorStand();
            if (ballArmorStand == null) return;

            final Slime hitbox = soccerBall.getBallArmorStand().getHitboxStand();
            if (hitbox == null) return;

            final Location hitboxLocation = hitbox.getLocation();

            if (!goalCuboid.containsLocation(hitboxLocation)) return;

            final Firework firework = (Firework) hitboxLocation.getWorld().spawnEntity(hitboxLocation, EntityType.FIREWORK);
            final FireworkMeta fireworkMeta = firework.getFireworkMeta();
            final FireworkEffect effect = FireworkEffect.builder().withColor(Color.YELLOW).withFade(Color.YELLOW).with(Type.BALL_LARGE).build();

            fireworkMeta.addEffect(effect);
            fireworkMeta.setPower(3);
            firework.setFireworkMeta(fireworkMeta);

            soccerBall.despawn();

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

                soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] &b" + ballTagger.getName() + " (Referee) &7just scored a goal! This is not allowed!");
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

            // Check if goalCuboid is the same as the taggerTeam goalCuboid
            if (goalCuboid == taggerTeam.getGoalCuboid()) { // Auto goal
                final ISoccerTeam opposite = teamHelper.getOppositeTeam(taggerTeam);
                opposite.setGoals(opposite.getGoals() + 1);

                final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
                for (final ISoccerTeam soccerTeam : teamHelper.getTeamList()) {
                    teamScoreBuilder
                            .append(ChatColor.valueOf(soccerTeam.getColor()))
                            .append(soccerTeam.getGoals())
                            .append(" &f- ");
                }
                // Remove the last 5 characters (space, dash, space, bracket, space)
                final String score = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

                soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] " + ChatColor.valueOf(taggerTeam.getColor()) + ballTagger.getName() + " &7just scored an own goal!");
                soccerArena.getArenaAnnouncer().sendGlobalTitle(
                        ChatColor.valueOf(taggerTeam.getColor()) + String.valueOf(ChatColor.BOLD) + "OWN GOAL!",
                        score,
                        0,
                        40,
                        0
                );

                Bukkit.getScheduler().runTaskLater(soccerArena.getSoccerMiniGame().getHoloSportsGame(), new BallSpawnTask(soccerArena, soccerBall), 60L);
                soccerArena.resetPositions();

                return;
            }

            taggerTeam.setGoals(taggerTeam.getGoals() + 1);

            final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
            for (final ISoccerTeam soccerTeam : teamHelper.getTeamList()) {
                teamScoreBuilder
                        .append(ChatColor.valueOf(soccerTeam.getColor()))
                        .append(soccerTeam.getGoals())
                        .append(" &f- ");
            }
            // Remove the last 5 characters (space, dash, space, bracket, space)
            final String score = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] " + ChatColor.valueOf(taggerTeam.getColor()) + ballTagger.getName() + " &7scored a goal for their team!");
            soccerArena.getArenaAnnouncer().sendGlobalTitle(
                    ChatColor.valueOf(taggerTeam.getColor()) + String.valueOf(ChatColor.BOLD) + "GOAL!",
                    score,
                    0,
                    40,
                    0
            );

            Bukkit.getScheduler().runTaskLater(soccerArena.getSoccerMiniGame().getHoloSportsGame(), new BallSpawnTask(soccerArena, soccerBall), 60L);

            soccerArena.resetPositions();
        });
    }
}
