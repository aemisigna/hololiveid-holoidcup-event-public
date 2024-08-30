package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.commons.util.BlockUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.task.line.BallExitLineType;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public final class BallExitTickTask implements Runnable {
    private final ISoccerPlayerHelper playerHelper;
    private final ISoccerTeamHelper teamHelper;
    private final SoccerArena soccerArena;

    public BallExitTickTask(final SoccerArena arena) {
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

        final Cuboid leftSideCuboid = soccerArena.getLeftSideCuboid();
        final Cuboid rightSideCuboid = soccerArena.getRightSideCuboid();
        final Cuboid redEndCuboid = soccerArena.getRedEndCuboid();
        final Cuboid blueEndCuboid = soccerArena.getBlueEndCuboid();

        final Location lastBallLocation = soccerBall.getBallArmorStand().getHitboxStand().getLocation();

        if (soccerArena.getArenaCuboid().containsLocation(lastBallLocation)) return;

        boolean flagged = false;

        for (final Cuboid cuboid : teamHelper.getGoalCuboids()) {
            // If the ball location is in the goal cuboid, then flag
            if (cuboid.containsLocation(lastBallLocation)) {
                flagged = true;
                break;
            }
        }

        if (flagged) return;
        if (soccerBall.isGraced()) return;

        if (leftSideCuboid.containsLocation(lastBallLocation)) {
            sideThrow(lastBallLocation, soccerBall, ballTagger, BallExitLineType.LEFT_SIDE);
        }

        if (rightSideCuboid.containsLocation(lastBallLocation)) {
            sideThrow(lastBallLocation, soccerBall, ballTagger, BallExitLineType.RIGHT_SIDE);
        }

        if (redEndCuboid.containsLocation(lastBallLocation)) { // Corner kick
            if (taggerTeam.getIdentifier().equalsIgnoreCase("white")) {
                goalThrow(lastBallLocation, soccerBall, ballTagger, BallExitLineType.RED_GOAL);
                return;
            }
            cornerThrow(lastBallLocation, soccerBall, ballTagger, BallExitLineType.RED_CORNER);
        }

        if (blueEndCuboid.containsLocation(lastBallLocation)) {
            if (taggerTeam.getIdentifier().equalsIgnoreCase("red")) {
                goalThrow(lastBallLocation, soccerBall, ballTagger, BallExitLineType.BLUE_GOAL);
                return;
            }
            cornerThrow(lastBallLocation, soccerBall, ballTagger, BallExitLineType.BLUE_CORNER);
        }
    }

    private void sideThrow(final Location lastBallLocation, final SoccerBall soccerBall, final ISoccerPlayer ballTagger, final BallExitLineType ballExitLineType) {
        final List<Block> nearbyWhiteConcreteBlocks = BlockUtil.getNearbyBlocks(lastBallLocation, Material.WHITE_CONCRETE, 100);
        if (nearbyWhiteConcreteBlocks.isEmpty()) {
            soccerBall.despawn();
            soccerBall.spawn(soccerArena.getBallSpawnLocation());
            return;
        }

        nearbyWhiteConcreteBlocks.sort((a, b) -> {
            final double aDistance = a.getLocation().distance(lastBallLocation);
            final double bDistance = b.getLocation().distance(lastBallLocation);

            return Double.compare(aDistance, bDistance);
        });

        // Get the closest block, if it not exists, then teleport the ball to the spawn location
        if (nearbyWhiteConcreteBlocks.size() == 0) {
            soccerBall.despawn();
            soccerBall.spawn(soccerArena.getBallSpawnLocation());
            return;
        }

        final Block closestBlock = nearbyWhiteConcreteBlocks.get(0);
        final Location blockLocation = closestBlock.getLocation();
        blockLocation.add(blockLocation.getX() > 0 ? 0.5 : -0.5, 0.0, blockLocation.getZ() > 0 ? 0.5 : -0.5);

        blockLocation.setY(66);

        soccerBall.despawn();
        soccerBall.spawn(blockLocation);

        soccerBall.setGraced(true);

        soccerArena.getArenaAnnouncer().sendGlobalTitle("Side Throw",
                ChatColor.valueOf(ballTagger.getTeam().getColor()) + ballTagger.getName(),
                0,
                30,
                0
        );

        teleportNearestPlayer(soccerBall, ballTagger, ballExitLineType);
    }

    private void cornerThrow(final Location lastBallLocation, final SoccerBall soccerBall, final ISoccerPlayer ballTagger, final BallExitLineType ballExitLineType) {
        final List<Block> nearbyRedstoneBlocks = BlockUtil.getNearbyBlocks(lastBallLocation, Material.REDSTONE_BLOCK, 90);
        if (nearbyRedstoneBlocks.isEmpty()) {
            soccerBall.despawn();
            soccerBall.spawn(soccerArena.getBallSpawnLocation());
            return;
        }

        nearbyRedstoneBlocks.sort((a, b) -> {
            final double aDistance = a.getLocation().distance(lastBallLocation);
            final double bDistance = b.getLocation().distance(lastBallLocation);

            return Double.compare(aDistance, bDistance);
        });

        // Get the closest block, if it not exists, then teleport the ball to the spawn location
        if (nearbyRedstoneBlocks.size() == 0) {
            soccerBall.despawn();
            soccerBall.spawn(soccerArena.getBallSpawnLocation());

            return;
        }

        final Block closestBlock = nearbyRedstoneBlocks.get(0);
        final Location blockLocation = closestBlock.getLocation();
        blockLocation.add(blockLocation.getX() > 0 ? 0.5 : -0.5, 0.0, blockLocation.getZ() > 0 ? 0.5 : -0.5);

        blockLocation.setY(66);

        soccerBall.despawn();
        soccerBall.spawn(blockLocation);

        soccerBall.setGraced(true);

        teleportNearestPlayer(soccerBall, ballTagger, ballExitLineType);
    }

    private void goalThrow(final Location lastBallLocation, final SoccerBall soccerBall, final ISoccerPlayer ballTagger, final BallExitLineType ballExitLineType) {
        final List<Block> nearbyPowderBlocks = BlockUtil.getNearbyBlocks(lastBallLocation, Material.WHITE_CONCRETE_POWDER, 50);
        // Get the closest block, if it not exists, then teleport the ball to the spawn location
        if (nearbyPowderBlocks.size() == 0) {
            soccerBall.despawn();
            soccerBall.spawn(soccerArena.getBallSpawnLocation());
            return;
        }

        nearbyPowderBlocks.sort((a, b) -> {
            final double aDistance = a.getLocation().distance(lastBallLocation);
            final double bDistance = b.getLocation().distance(lastBallLocation);

            return Double.compare(aDistance, bDistance);
        });

        final Block closestBlock = nearbyPowderBlocks.get(0);
        final Location blockLocation = closestBlock.getLocation();
        blockLocation.add(blockLocation.getX() > 0 ? 0.5 : -0.5, 0.0, blockLocation.getZ() > 0 ? 0.5 : -0.5);

        blockLocation.setY(66);

        soccerBall.despawn();
        soccerBall.spawn(blockLocation);

        soccerBall.setGraced(true);

        teleportNearestPlayerGoal(soccerBall, ballTagger, ballExitLineType);
    }

    private void teleportNearestPlayer(final SoccerBall ball, final ISoccerPlayer tagger, final BallExitLineType ballExitLineType) {
        final Location ballLocation = ball.getBallArmorStand().getHitboxStand().getLocation();

        final ISoccerTeam taggerTeam = tagger.getTeam();
        if (taggerTeam == null) return;

        final ISoccerTeam oppositeTeam = teamHelper.getOppositeTeam(taggerTeam);
        if (oppositeTeam == null) return;

        final List<Player> nearbyBallPlayers = ball.getBallArmorStand().getBaseStand().getNearbyEntities(130, 40, 130).stream().filter(
                entity -> entity instanceof Player
        ).map(entity -> (Player) entity).toList();

        if (nearbyBallPlayers.isEmpty()) return;

        // Compare every nearbyBallPlayers locations and find the closest one to the ball using a comparator
        Optional<Player> nearestPlayerOptional = nearbyBallPlayers.stream().filter(player -> {
            final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (soccerPlayerOptional.isEmpty()) return false;

            final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
            final ISoccerTeam playerTeam = soccerPlayer.getTeam();
            if (playerTeam == null) return false;

            if (soccerPlayer.isReferee()) return false;

            if (soccerPlayer.getRole() == SoccerRole.GOALKEEPER) return false;

            return playerTeam.equals(oppositeTeam);
        }).min((player1, player2) -> {
            final Location player1Location = player1.getLocation();
            final Location player2Location = player2.getLocation();

            final double player1Distance = player1Location.distance(ballLocation);
            final double player2Distance = player2Location.distance(ballLocation);

            return Double.compare(player1Distance, player2Distance);
        });

        if (nearestPlayerOptional.isEmpty()) {
            nearestPlayerOptional = nearbyBallPlayers.stream().filter(player -> {
                final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
                if (soccerPlayerOptional.isEmpty()) return false;

                final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
                final ISoccerTeam playerTeam = soccerPlayer.getTeam();
                if (playerTeam == null) return false;

                if (soccerPlayer.isReferee()) return false;

                return playerTeam.equals(oppositeTeam);
            }).min((player1, player2) -> {
                final Location player1Location = player1.getLocation();
                final Location player2Location = player2.getLocation();

                final double player1Distance = player1Location.distance(ballLocation);
                final double player2Distance = player2Location.distance(ballLocation);

                return Double.compare(player1Distance, player2Distance);
            });
        }

        if (nearestPlayerOptional.isEmpty()) return;

        final Player nearestPlayer = nearestPlayerOptional.get();

        final Optional<ISoccerPlayer> nearestSoccerPlayerOptional = playerHelper.getPlayer(nearestPlayer.getUniqueId());
        if (nearestSoccerPlayerOptional.isEmpty()) return;

        final ISoccerPlayer nearestSoccerPlayer = nearestSoccerPlayerOptional.get();

        // Create a switch to decide which title to send between Corner or Side

        switch (ballExitLineType) {
            case LEFT_SIDE, RIGHT_SIDE -> {
                soccerArena.getArenaAnnouncer().sendGlobalTitle("Side Throw",
                        ChatColor.valueOf(nearestSoccerPlayer.getTeam().getColor()) + nearestSoccerPlayer.getName(),
                        0,
                        30,
                        0
                );
            }
            case RED_CORNER, BLUE_CORNER -> {
                soccerArena.getArenaAnnouncer().sendGlobalTitle("Corner Kick",
                        ChatColor.valueOf(nearestSoccerPlayer.getTeam().getColor()) + nearestSoccerPlayer.getName(),
                        0,
                        30,
                        0
                );
            }
            case RED_GOAL, BLUE_GOAL -> {
                soccerArena.getArenaAnnouncer().sendGlobalTitle("Goal Kick",
                        ChatColor.valueOf(nearestSoccerPlayer.getTeam().getColor()) + nearestSoccerPlayer.getName(),
                        0,
                        30,
                        0
                );
            }
        }

        final Location closeBallLocation = ballLocation.clone();
        switch (ballExitLineType) {
            case LEFT_SIDE -> {
                closeBallLocation.setX(closeBallLocation.getX() + 1);
                closeBallLocation.setZ(closeBallLocation.getZ() - 1);
            }
            case RIGHT_SIDE -> {
                closeBallLocation.setX(closeBallLocation.getX() - 1);
                closeBallLocation.setZ(closeBallLocation.getZ() + 1);

                closeBallLocation.setYaw(-180F);
            }

            case RED_CORNER -> {
                closeBallLocation.setX(closeBallLocation.getX() - 1);
                closeBallLocation.setZ(closeBallLocation.getZ() + 1);

                closeBallLocation.setYaw(145F);
                closeBallLocation.setPitch(-0.5F);
            }
            case BLUE_CORNER -> {
                closeBallLocation.setX(closeBallLocation.getX() + 1);
                closeBallLocation.setZ(closeBallLocation.getZ() - 1);

                closeBallLocation.setYaw(-145F);
                closeBallLocation.setPitch(-4F);
            }
        }

        nearestPlayer.teleport(closeBallLocation);

        ball.setMandatoryTagger(nearestSoccerPlayer);
    }

    private void teleportNearestPlayerGoal(final SoccerBall ball, final ISoccerPlayer tagger, final BallExitLineType ballExitLineType) {
        final Location ballLocation = ball.getBallArmorStand().getHitboxStand().getLocation();

        final ISoccerTeam taggerTeam = tagger.getTeam();
        if (taggerTeam == null) return;

        final ISoccerTeam oppositeTeam = teamHelper.getOppositeTeam(taggerTeam);
        if (oppositeTeam == null) return;

        final List<Player> nearbyBallPlayers = ball.getBallArmorStand().getBaseStand().getNearbyEntities(180, 50, 180).stream().filter(
                entity -> entity instanceof Player
        ).map(entity -> (Player) entity).toList();

        if (nearbyBallPlayers.isEmpty()) return;

        // Compare every nearbyBallPlayers locations and find the closest one to the ball using a comparator
        Optional<Player> nearestPlayerOptional = nearbyBallPlayers.stream().filter(player -> {
            final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (soccerPlayerOptional.isEmpty()) return false;

            final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
            final ISoccerTeam playerTeam = soccerPlayer.getTeam();
            if (playerTeam == null) return false;

            if (soccerPlayer.isReferee()) return false;

            if (soccerPlayer.getRole() != SoccerRole.GOALKEEPER) return false;

            return playerTeam.equals(oppositeTeam);
        }).min((player1, player2) -> {
            final Location player1Location = player1.getLocation();
            final Location player2Location = player2.getLocation();

            final double player1Distance = player1Location.distance(ballLocation);
            final double player2Distance = player2Location.distance(ballLocation);

            return Double.compare(player1Distance, player2Distance);
        });

        if (nearestPlayerOptional.isEmpty()) {
            nearestPlayerOptional = nearbyBallPlayers.stream().filter(player -> {
                final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
                if (soccerPlayerOptional.isEmpty()) return false;

                final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
                final ISoccerTeam playerTeam = soccerPlayer.getTeam();
                if (playerTeam == null) return false;

                if (soccerPlayer.isReferee()) return false;

                return playerTeam.equals(oppositeTeam);
            }).min((player1, player2) -> {
                final Location player1Location = player1.getLocation();
                final Location player2Location = player2.getLocation();

                final double player1Distance = player1Location.distance(ballLocation);
                final double player2Distance = player2Location.distance(ballLocation);

                return Double.compare(player1Distance, player2Distance);
            });
        }

        if (nearestPlayerOptional.isEmpty()) return;

        final Player nearestPlayer = nearestPlayerOptional.get();

        final Optional<ISoccerPlayer> nearestSoccerPlayerOptional = playerHelper.getPlayer(nearestPlayer.getUniqueId());
        if (nearestSoccerPlayerOptional.isEmpty()) return;

        final ISoccerPlayer nearestSoccerPlayer = nearestSoccerPlayerOptional.get();

        // Create a switch to decide which title to send between Corner or Side

        switch (ballExitLineType) {
            case LEFT_SIDE, RIGHT_SIDE -> {
                soccerArena.getArenaAnnouncer().sendGlobalTitle("Side Throw",
                        ChatColor.valueOf(nearestSoccerPlayer.getTeam().getColor()) + nearestSoccerPlayer.getName(),
                        0,
                        30,
                        0
                );
            }
            case RED_CORNER, BLUE_CORNER -> {
                soccerArena.getArenaAnnouncer().sendGlobalTitle("Corner Kick",
                        ChatColor.valueOf(nearestSoccerPlayer.getTeam().getColor()) + nearestSoccerPlayer.getName(),
                        0,
                        30,
                        0
                );
            }
            case RED_GOAL, BLUE_GOAL -> {
                soccerArena.getArenaAnnouncer().sendGlobalTitle("Goal Kick",
                        ChatColor.valueOf(nearestSoccerPlayer.getTeam().getColor()) + nearestSoccerPlayer.getName(),
                        0,
                        30,
                        0
                );
            }
        }

        final Location closeBallLocation = ballLocation.clone();
        switch (ballExitLineType) {
            case LEFT_SIDE -> {
                closeBallLocation.setX(closeBallLocation.getX() + 1);
                closeBallLocation.setZ(closeBallLocation.getZ() - 1);
            }
            case RIGHT_SIDE -> {
                closeBallLocation.setX(closeBallLocation.getX() - 1);
                closeBallLocation.setZ(closeBallLocation.getZ() + 1);

                closeBallLocation.setYaw(-180F);
            }

            case RED_CORNER -> {
                closeBallLocation.setX(closeBallLocation.getX() - 1);
                closeBallLocation.setZ(closeBallLocation.getZ() + 1);

                closeBallLocation.setYaw(145F);
                closeBallLocation.setPitch(-0.5F);
            }
            case BLUE_CORNER -> {
                closeBallLocation.setX(closeBallLocation.getX() + 1);
                closeBallLocation.setZ(closeBallLocation.getZ() - 1);

                closeBallLocation.setYaw(-145F);
                closeBallLocation.setPitch(-4F);
            }
        }

        nearestPlayer.teleport(closeBallLocation);

        ball.setMandatoryTagger(nearestSoccerPlayer);
    }
}
