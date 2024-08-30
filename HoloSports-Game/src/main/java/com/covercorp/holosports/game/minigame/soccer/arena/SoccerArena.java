package com.covercorp.holosports.game.minigame.soccer.arena;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.arena.command.*;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.announcer.ArenaAnnouncer;
import com.covercorp.holosports.game.minigame.soccer.arena.properties.SoccerMatchProperties;
import com.covercorp.holosports.game.minigame.soccer.arena.task.*;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import com.covercorp.holosports.game.util.NicePlayersUtil;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

@Getter(AccessLevel.PUBLIC)
public final class SoccerArena {
    private final SoccerMiniGame soccerMiniGame;
    private final ISoccerPlayerHelper playerHelper;
    private final ISoccerTeamHelper teamHelper;

    private final SoccerMatchProperties soccerMatchProperties;

    @Setter(AccessLevel.PUBLIC) private SoccerMatchState state;

    private final Location lobbyLocation;
    private final Location ballSpawnLocation;
    private final Cuboid arenaCuboid;

    private final Cuboid leftSideCuboid;
    private final Cuboid rightSideCuboid;
    private final Cuboid redEndCuboid;
    private final Cuboid blueEndCuboid;

    private @Nullable SoccerBall soccerBall;

    private final ArenaAnnouncer arenaAnnouncer;

    @Setter(AccessLevel.PUBLIC) private int gameTime = 0;
    @Setter(AccessLevel.PUBLIC) private int timePerHalf = 300; // 600

    @Setter(AccessLevel.PUBLIC) private int playedMatches = 0;

    @Setter(AccessLevel.PUBLIC) private boolean penaltyMode = false;
    @Setter(AccessLevel.PUBLIC) private ISoccerPlayer penaltyKicker = null;

    private final Location penaltyBallSpawnLocation;
    private final Location penaltyGoalkeeperSpawnLocation;
    private final Location penaltyShooterSpawnLocation;

    private final Cuboid penaltyPlayZoneCuboid;
    private final Cuboid penaltyGoalZoneCuboid;

    private int matchTickTaskId;
    private int matchTimeTaskId;
    private int exitTaskId;
    private int goalTaskId;
    private int actionbarTaskId;
    private int fireworkTaskId;

    public SoccerArena(final SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;
        this.playerHelper = soccerMiniGame.getPlayerHelper();
        this.teamHelper = soccerMiniGame.getTeamHelper();

        soccerMatchProperties = new SoccerMatchProperties(this);

        state = SoccerMatchState.WAITING;

        lobbyLocation = soccerMiniGame.getSoccerConfigHelper().getLobbySpawn();
        ballSpawnLocation = soccerMiniGame.getSoccerConfigHelper().getStartBallSpawn();
        arenaCuboid = soccerMiniGame.getSoccerConfigHelper().getArenaCuboid();

        leftSideCuboid = soccerMiniGame.getSoccerConfigHelper().getLeftSideCuboid();
        rightSideCuboid = soccerMiniGame.getSoccerConfigHelper().getRightSideCuboid();
        blueEndCuboid = soccerMiniGame.getSoccerConfigHelper().getFirstTeamEndCuboid();
        redEndCuboid = soccerMiniGame.getSoccerConfigHelper().getSecondTeamEndCuboid();

        // Penalty
        penaltyBallSpawnLocation = soccerMiniGame.getSoccerConfigHelper().getPenaltyBallSpawn();
        penaltyGoalkeeperSpawnLocation = soccerMiniGame.getSoccerConfigHelper().getPenaltyGoalkeeperSpawn();
        penaltyShooterSpawnLocation = soccerMiniGame.getSoccerConfigHelper().getPenaltyShooterSpawn();

        penaltyPlayZoneCuboid = soccerMiniGame.getSoccerConfigHelper().getPenaltyPlayZone();
        penaltyGoalZoneCuboid = soccerMiniGame.getSoccerConfigHelper().getPenaltyGoalZone();

        arenaAnnouncer = new ArenaAnnouncer(soccerMiniGame.getPlayerHelper());

        // COMMANDS
        new AddPlayerToTeamCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new RemovePlayerToTeamCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new ClearTeamPlayersCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new SetPlayerRoleCommand(this).register(soccerMiniGame.getHoloSportsGame());

        // GLOBAL COMMANDS
        new StartMatchCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new StopMatchCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new ResumeMatchCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new ForceEndMatchCommand(this).register(soccerMiniGame.getHoloSportsGame());
        new SetMatchTimeCommand(this).register(soccerMiniGame.getHoloSportsGame());

        // DEBUG
        /*
        BukkitCommandBuilder.name("soccer:debug-players").register(soccerMiniGame.getHoloSportsGame(), (sender, args) -> {
            sender.sendMessage(ChatColor.YELLOW + "Showing actual player list:");

            soccerMiniGame.getPlayerHelper().getPlayerList().forEach(soccerPlayer -> {
                sender.sendMessage(CommonUtil.colorize("&7- &8" + soccerPlayer.getName() + " &7[" + soccerPlayer.getTeam().getName() + " - " + soccerPlayer.getRole() + "]"));
            });

            sender.sendMessage(ChatColor.YELLOW + "-------------------------------------");
        });*/
    }

    public void start() {
        if (soccerBall != null) soccerBall.despawn();

        final ISoccerPlayerHelper playerHelper = soccerMiniGame.getPlayerHelper();

        // Teleport players
        resetPositions();

        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (soccerPlayerOptional.isEmpty()) {
                player.setAllowFlight(true);
                player.sendMessage(CommonUtil.colorize("&a[!] You are now spectating the match! You can fly!"));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0F);
            }
        });

        playerHelper.getPlayerList().forEach(soccerPlayer -> {
            final ISoccerTeam team = soccerPlayer.getTeam();
            if (team == null) return;

            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.sendTitle(CommonUtil.colorize("&aStart!"), "", 0, 30, 0);

            // Make sure that they don't fly
            player.setAllowFlight(false);
        });

        arenaAnnouncer.sendGlobalTitle("&6&lSOCCER", "&7Good luck!", 0, 30, 10);

        arenaAnnouncer.sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        arenaAnnouncer.sendGlobalCenteredMessage("&f&lSoccer");
        arenaAnnouncer.sendGlobalMessage("&0 ");
        arenaAnnouncer.sendGlobalCenteredMessage("&e&lKick the ball into the other team's goal to score a goal.");
        arenaAnnouncer.sendGlobalCenteredMessage("&e&lThe team with the most goals at the end of the game wins the match!");
        arenaAnnouncer.sendGlobalMessage("&0 ");
        arenaAnnouncer.sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        gameTime = 0;
        playedMatches = 0;
        penaltyMode = false;

        soccerBall = new SoccerBall();
        soccerBall.spawn(ballSpawnLocation);

        startTasks();

        Bukkit.getScheduler().cancelTask(soccerMatchProperties.getStartingTaskId());

        setState(SoccerMatchState.GAME);
    }

    public void stop() {
        setState(SoccerMatchState.ENDING);

        if (soccerBall != null) soccerBall.despawn();

        soccerBall = null;
        playedMatches = 0;
        penaltyMode = false;

        stopTasks();

        final ISoccerPlayerHelper playerHelper = soccerMiniGame.getPlayerHelper();
        final ISoccerTeamHelper teamHelper = soccerMiniGame.getTeamHelper();

        final Optional<ISoccerTeam> winnerTeamOptional = teamHelper.getTeamWithMostGoals();

        final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
        for (final ISoccerTeam soccerTeam : teamHelper.getTeamList()) {
            teamScoreBuilder
                    .append(ChatColor.valueOf(soccerTeam.getColor()))
                    .append(soccerTeam.getGoals())
                    .append(" &f- ");
        }
        // Remove the last 5 characters (space, dash, space, bracket, space)
        final String score = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

        if (winnerTeamOptional.isEmpty()) {
            arenaAnnouncer.sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            arenaAnnouncer.sendGlobalCenteredMessage("&e&lMatch ended!");
            arenaAnnouncer.sendGlobalCenteredMessage(" ");
            arenaAnnouncer.sendGlobalCenteredMessage("&eDRAW! There's no winner!");
            arenaAnnouncer.sendGlobalCenteredMessage(score);
            arenaAnnouncer.sendGlobalCenteredMessage(" ");
            arenaAnnouncer.sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.8F));
            arenaAnnouncer.sendGlobalMessage(" \n&6&lThe Match ended in a DRAW.");
        } else {
            final ISoccerTeam winnerTeam = winnerTeamOptional.get();
            arenaAnnouncer.sendGlobalTitle("&aGame ended!", ChatColor.valueOf(winnerTeam.getColor()) + "Team " + winnerTeam.getName() + " &7won the match!", 0, 100, 20);

            arenaAnnouncer.sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            arenaAnnouncer.sendGlobalCenteredMessage("&e&lMatch ended!");
            arenaAnnouncer.sendGlobalCenteredMessage(" ");
            arenaAnnouncer.sendGlobalCenteredMessage("&eWinner team: " + ChatColor.valueOf(winnerTeam.getColor()) + winnerTeam.getName());
            arenaAnnouncer.sendGlobalCenteredMessage("&7" + winnerTeam.getPlayers().stream().map(ISoccerPlayer::getName).collect(Collectors.joining(" &f&&7 ")));
            arenaAnnouncer.sendGlobalCenteredMessage(score);
            arenaAnnouncer.sendGlobalCenteredMessage(" ");
            arenaAnnouncer.sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.8F));
            fireworkTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new MatchWinnerFireworksTask(this, winnerTeam), 0L, 20L).getTaskId();
        }

        Bukkit.getScheduler().runTaskLater(soccerMiniGame.getHoloSportsGame(), () -> {
            playerHelper.getPlayerList().forEach(soccerPlayer -> {
                final ISoccerTeam team = soccerPlayer.getTeam();

                if (team != null) teamHelper.removePlayerFromTeam(soccerPlayer, team.getIdentifier());

                playerHelper.removePlayer(soccerPlayer.getUniqueId());
            });

            // Get all teams, clear all the players if there are and set the goals to 0
            teamHelper.getTeamList().forEach(team -> {
                if (team.getPlayers().size() != 0) team.getPlayers().forEach(teamPlayer -> teamHelper.removePlayerFromTeam(teamPlayer, team.getIdentifier()));
                team.setGoals(0);
                team.setPenalties(0);
                team.getPenaltyRotationQueue().clear();
            });

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setGameMode(GameMode.ADVENTURE);

                player.setAllowFlight(false);
                player.getInventory().clear();

                if (NicePlayersUtil.isNicePlayer(player)) {
                    final Inventory inventory = player.getInventory();

                    inventory.setItem(4, SoccerGameItemCollection.getStartItem());
                    inventory.setItem(5, SoccerGameItemCollection.getResumeItem());
                    inventory.setItem(6, SoccerGameItemCollection.getStopItem());
                }

                player.teleport(lobbyLocation);
            });

            // Reset starting properties
            getSoccerMatchProperties().resetTimer();

            setState(SoccerMatchState.WAITING);

            Bukkit.getScheduler().cancelTask(fireworkTaskId);
        }, 120L);
    }

    public void pause() {
        setState(SoccerMatchState.PAUSED);

        stopTasks();

        if (soccerBall != null) soccerBall.despawn();
        soccerBall = null;

        // Use the Announcer to announce the game paused and the referee must continue it
        arenaAnnouncer.sendGlobalMessage("&7[!] The game has been paused.");

        arenaAnnouncer.sendGlobalSound(Sound.BLOCK_CHEST_OPEN, 0.6F, 0.6F);
    }

    public void resume() {
        if (soccerBall != null) soccerBall.despawn();

        soccerBall = new SoccerBall();

        if (penaltyMode) {
            setState(SoccerMatchState.GAME);

            startPenalties();
            return;
        }

        soccerBall.spawn(ballSpawnLocation);

        startTasks();

        setState(SoccerMatchState.GAME);

        resetPositions();

        arenaAnnouncer.sendGlobalTitle("&aGame resumed!", "&7Good luck!", 0, 60, 0);
        arenaAnnouncer.sendGlobalSound(Sound.ITEM_GOAT_HORN_SOUND_0, 0.6F, 0.6F);
    }

    public void startPenalties() {
        final ISoccerPlayerHelper playerHelper = soccerMiniGame.getPlayerHelper();
        final ISoccerTeamHelper teamHelper = soccerMiniGame.getTeamHelper();

        // Check if some team have less than 2 participants, if so, cancel the match as a draw
        if (teamHelper.getTeamList().stream().anyMatch(team -> team.getPlayers().size() < 2)) {
            arenaAnnouncer.sendGlobalMessage("&c&l[!] The match has been canceled due to lack of players. All teams must have at least 2 players to play Soccer Penalty Mode.");
            arenaAnnouncer.sendGlobalSound(Sound.BLOCK_CHEST_OPEN, 0.6F, 0.6F);

            stop();
            return;
        }

        final Random random = new Random();

        arenaAnnouncer.sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        arenaAnnouncer.sendGlobalCenteredMessage("&f&lSoccer Penalty Mode");
        arenaAnnouncer.sendGlobalMessage("&0 ");
        arenaAnnouncer.sendGlobalCenteredMessage("&e&lKick the ball to the goal to score a penalty.");
        arenaAnnouncer.sendGlobalCenteredMessage("&e&lThe first team to reach 5 penalty goals wins.");
        arenaAnnouncer.sendGlobalMessage("&0 ");
        arenaAnnouncer.sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        arenaAnnouncer.sendGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);

        // Cancel the exit and goal tasks
        Bukkit.getScheduler().cancelTask(exitTaskId);
        exitTaskId = -1;
        Bukkit.getScheduler().cancelTask(goalTaskId);
        goalTaskId = -1;

        // Run the special penalty Exit and Goal tasks
        startPenaltyTasks();

        // Check if any team doesn't have any goalkeeper
        teamHelper.getTeamList().forEach(team -> {
            if (team.getGoalkeepers().size() == 0) {
                final ISoccerPlayer randomPlayer = team.getStandards().stream().toList().get(random.nextInt(team.getPlayers().size()));
                randomPlayer.setRole(SoccerRole.GOALKEEPER);
            }
            if (team.getGoalkeepers().size() > 1) {
                final ISoccerPlayer randomPlayer = team.getGoalkeepers().stream().toList().get(random.nextInt(team.getGoalkeepers().size()));
                team.getGoalkeepers().forEach(goalkeeper -> {
                    if (goalkeeper != randomPlayer) {
                        goalkeeper.setRole(SoccerRole.STANDARD);
                    }
                });
            }
        });

        // Instance lists
        // Select a random team to start the penalties
        final ISoccerTeam startingPenaltyTeam = teamHelper.getTeamList().get(random.nextInt(teamHelper.getTeamList().size()));
        // Select a random STANDARD player from the team
        //final ISoccerPlayer startingPenaltyPlayer = startingPenaltyTeam.getStandards().get(random.nextInt(startingPenaltyTeam.getStandards().size()));

        // Fill the rotation queue
        final Queue<ISoccerPlayer> startingTeamRotationQueue = startingPenaltyTeam.getPenaltyRotationQueue();
        startingTeamRotationQueue.clear();

        // Add every player that is not a goalkeeper to the rotation queue
        startingPenaltyTeam.getPlayers().forEach(player -> {
            if (player.getRole() != SoccerRole.GOALKEEPER && !player.isReferee()) {
                startingTeamRotationQueue.add(player);
            }
        });

        final ISoccerPlayer startingPenaltyPlayer = startingTeamRotationQueue.poll();
        startingTeamRotationQueue.add(startingPenaltyPlayer);

        setPenaltyKicker(startingPenaltyPlayer);

        // Fill the opponent rotation queue
        final ISoccerTeam opponentPenaltyTeam = teamHelper.getOppositeTeam(startingPenaltyTeam);
        if (opponentPenaltyTeam == null) return;

        final Queue<ISoccerPlayer> opponentTeamRotationQueue = opponentPenaltyTeam.getPenaltyRotationQueue();
        opponentTeamRotationQueue.clear();

        opponentPenaltyTeam.getPlayers().forEach(player -> {
            if (player.getRole() != SoccerRole.GOALKEEPER && !player.isReferee()) {
                opponentTeamRotationQueue.add(player);
            }
        });

        // Teleport all players to the penalty spawn
        playerHelper.getPlayerList().forEach(participant -> {
            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            SoccerGameItemCollection.setupPlayerHotbar(this, participant, participant.getRole());

            player.setGameMode(GameMode.SPECTATOR);
        });

        arenaAnnouncer.sendGlobalMessage("&7[!] Shooter is now &a" + penaltyKicker.getName() + "&7!");
        arenaAnnouncer.sendGlobalMessage("&eThe ball will be spawned in &b5 seconds&e!");

        Bukkit.getScheduler().runTaskLater(soccerMiniGame.getHoloSportsGame(), new BallExecutePenaltyTask(soccerMiniGame, this), 100L);
    }

    /**
     * Rotate the penalty kicker
     * @apiNote This method is called when the penalty kicker has scored a goal
     * @see BallPenaltyGoalTickTask
     */
    public void rotatePenaltyKicker() {
        arenaAnnouncer.sendGlobalMessage("&7[!] Rotating kicker without referees...");

        final ISoccerTeamHelper teamHelper = soccerMiniGame.getTeamHelper();

        // Get the last penalty kicker
        final ISoccerPlayer lastPenaltyKicker = getPenaltyKicker();
        if (penaltyKicker == null) return;
        final Player lastPenaltyKickerPlayer = Bukkit.getPlayer(lastPenaltyKicker.getUniqueId());
        if (lastPenaltyKickerPlayer != null) lastPenaltyKickerPlayer.setGameMode(GameMode.SPECTATOR);

        // Get the last penalty kicker's team
        final ISoccerTeam lastPenaltyKickerTeam = lastPenaltyKicker.getTeam();
        if (lastPenaltyKickerTeam == null) return;

        // Get the opposite team (the team that will shoot)
        final ISoccerTeam oppositeTeam = teamHelper.getOppositeTeam(lastPenaltyKickerTeam);
        // Get the opposite goalkeeper and set them to spectator
        if (oppositeTeam.getGoalkeepers().size() > 0) {
            final ISoccerPlayer oppositeGoalkeeper = oppositeTeam.getGoalkeepers().get(0);
            final Player oppositeGoalkeeperPlayer = Bukkit.getPlayer(oppositeGoalkeeper.getUniqueId());
            if (oppositeGoalkeeperPlayer != null) oppositeGoalkeeperPlayer.setGameMode(GameMode.SPECTATOR);
        }

        // Get a random STANDARD player from the opposite team
        //final ISoccerPlayer randomPlayer = oppositeTeam.getStandards().get(random.nextInt(oppositeTeam.getStandards().size()));

        // Get the next player in the rotation queue
        final ISoccerPlayer nextPenaltyKicker = oppositeTeam.getPenaltyRotationQueue().poll();
        oppositeTeam.getPenaltyRotationQueue().add(nextPenaltyKicker);

        setPenaltyKicker(nextPenaltyKicker);

        arenaAnnouncer.sendGlobalMessage("&7[!] Shooter is now &a" + penaltyKicker.getName() + "&7!");
        arenaAnnouncer.sendGlobalMessage("&eThe ball will be spawned in &b5 seconds&e!");

        Bukkit.getScheduler().runTaskLater(soccerMiniGame.getHoloSportsGame(), new BallExecutePenaltyTask(soccerMiniGame, this), 100L);
    }

    public boolean shouldDoPenalties() {
        // Check if the teams are tied, return true if they are, false if they aren't
        return soccerMiniGame.getTeamHelper().getTeamWithMostGoals().isEmpty();
    }

    private void startTasks() {
        matchTickTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new MatchTickTask(this), 0L, 1L).getTaskId();
        matchTimeTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new MatchTimeTask(this), 0L, 20L).getTaskId();
        exitTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new BallExitTickTask(this), 0L, 1L).getTaskId();
        goalTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new BallGoalTickTask(soccerMiniGame, this), 0L, 12L).getTaskId();
        actionbarTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new MatchActionBarTask(soccerMiniGame, this), 0L, 1L).getTaskId();
    }

    private void startPenaltyTasks() {
        exitTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new BallPenaltyExitTickTask(this), 0L, 1L).getTaskId();
        goalTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new BallPenaltyGoalTickTask(soccerMiniGame, this), 0L, 15L).getTaskId();
        matchTickTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new MatchTickTask(this), 0L, 1L).getTaskId();
        actionbarTaskId = Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new MatchActionBarTask(soccerMiniGame, this), 0L, 1L).getTaskId();
    }

    private void stopTasks() {
        Bukkit.getScheduler().cancelTask(matchTickTaskId);
        Bukkit.getScheduler().cancelTask(matchTimeTaskId);
        Bukkit.getScheduler().cancelTask(exitTaskId);
        Bukkit.getScheduler().cancelTask(goalTaskId);
        Bukkit.getScheduler().cancelTask(actionbarTaskId);
        Bukkit.getScheduler().cancelTask(soccerMatchProperties.getStartingTaskId());
    }

    public void resetPositions() {
        final ISoccerPlayerHelper playerHelper = soccerMiniGame.getPlayerHelper();
        playerHelper.getPlayerList().forEach(soccerPlayer -> {
            final ISoccerTeam team = soccerPlayer.getTeam();
            if (team == null) return;

            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            if (soccerPlayer.getRole() == null) soccerPlayer.setRole(SoccerRole.STANDARD);

            switch (soccerPlayer.getRole()) {
                case STANDARD -> {
                    final Location spawn = team.getStandardSpawns().get(0);
                    player.teleport(spawn);
                }
                case GOALKEEPER -> {
                    final Location spawn = team.getGoalKeeperSpawn();
                    player.teleport(spawn);
                }
                case REFEREE -> {
                    final Location spawn = soccerMiniGame.getSoccerConfigHelper().getRefereeSpawn();
                    player.teleport(spawn);
                }
            }
        });
    }
}