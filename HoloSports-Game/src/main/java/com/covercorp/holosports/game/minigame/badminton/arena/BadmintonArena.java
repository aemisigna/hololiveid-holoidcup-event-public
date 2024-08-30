package com.covercorp.holosports.game.minigame.badminton.arena;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.announcer.ArenaAnnouncer;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.BadmintonBall;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.hit.HitType;
import com.covercorp.holosports.game.minigame.badminton.arena.command.*;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.badminton.arena.listener.MatchListener;
import com.covercorp.holosports.game.minigame.badminton.arena.listener.ShuttlecockListener;
import com.covercorp.holosports.game.minigame.badminton.arena.properties.BadmintonMatchProperties;
import com.covercorp.holosports.game.minigame.badminton.arena.service.ScoreType;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.arena.task.*;
import com.covercorp.holosports.game.minigame.badminton.arena.task.arrow.DownArrowIndicatorTask;
import com.covercorp.holosports.game.minigame.badminton.arena.task.arrow.SideArrowIndicatorTask;
import com.covercorp.holosports.game.minigame.badminton.player.IBadmintonPlayerHelper;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.IBadmintonTeamHelper;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;

import com.covercorp.holosports.game.util.NicePlayersUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

@Getter(AccessLevel.PUBLIC)
public final class BadmintonArena {
    private final BadmintonMiniGame badmintonMiniGame;
    private final IBadmintonPlayerHelper playerHelper;
    private final IBadmintonTeamHelper teamHelper;

    private final BadmintonMatchProperties badmintonMatchProperties;
    private final Location lobbyLocation;

    private BadmintonBall badmintonBall;

    @Setter(AccessLevel.PUBLIC) private BadmintonMatchState state;

    @Setter(AccessLevel.PUBLIC) private BadmintonGameType gameType;

    private final ArenaAnnouncer arenaAnnouncer;

    private int floorFlagTime = 0;

    private int arenaTickTask = -1;
    private int arenaActionBarTask = -1;
    private int ballTrailTask = -1;
    private int ballParticleDebugTask = -1;

    private int ballLandTask = -1;
    private int fireworkTaskId = -1;

    private final List<SideArrowIndicatorTask> sideArrowIndicatorTasks;

    // GAME
    private IBadmintonTeam lastServiceTeam;

    public BadmintonArena(final BadmintonMiniGame badmintonMiniGame) {
        this.badmintonMiniGame = badmintonMiniGame;

        playerHelper = badmintonMiniGame.getPlayerHelper();
        teamHelper = badmintonMiniGame.getTeamHelper();

        badmintonMatchProperties = new BadmintonMatchProperties(this);

        state = BadmintonMatchState.WAITING;

        arenaAnnouncer = new ArenaAnnouncer(getPlayerHelper());

        lobbyLocation = badmintonMiniGame.getBadmintonConfigHelper().getLobbySpawn();

        sideArrowIndicatorTasks = new ArrayList<>();

        badmintonBall = null;

        if (lobbyLocation.getWorld() != null) clearOldBalls(lobbyLocation.getWorld());

        // Listeners
        Bukkit.getPluginManager().registerEvents(new MatchListener(badmintonMiniGame, this), badmintonMiniGame.getHoloSportsGame());
        Bukkit.getPluginManager().registerEvents(new ShuttlecockListener(this), badmintonMiniGame.getHoloSportsGame());

        // COMMANDS
        new AddPlayerToTeamCommand(this).register(badmintonMiniGame.getHoloSportsGame());
        new RemovePlayerToTeamCommand(this).register(badmintonMiniGame.getHoloSportsGame());
        new ClearTeamPlayersCommand(this).register(badmintonMiniGame.getHoloSportsGame());
        new ToggleParticlesCommand(this).register(badmintonMiniGame.getHoloSportsGame());
        new SetFloorToleranceTimeCommand(this).register(badmintonMiniGame.getHoloSportsGame());

        // GLOBAL COMMANDS
        new StartMatchCommand(this).register(badmintonMiniGame.getHoloSportsGame());
        new StopMatchCommand(this).register(badmintonMiniGame.getHoloSportsGame());
    }

    public void start() {
        if (badmintonBall != null) {
            badmintonBall.deSpawn();
            badmintonBall = null;
        }

        // Tasks
        arenaTickTask = Bukkit.getScheduler().runTaskTimer(badmintonMiniGame.getHoloSportsGame(), new BadmintonTickTask(this), 0L, 1L).getTaskId();
        arenaActionBarTask = Bukkit.getScheduler().runTaskTimer(badmintonMiniGame.getHoloSportsGame(), new MatchActionBarTask(badmintonMiniGame, this), 0L, 20L).getTaskId();
        ballTrailTask = Bukkit.getScheduler().runTaskTimer(badmintonMiniGame.getHoloSportsGame(), new ShuttlecockTrailTask(this), 0L, 2L).getTaskId();
        ballParticleDebugTask = Bukkit.getScheduler().runTaskTimer(badmintonMiniGame.getHoloSportsGame(), new ShuttlecockZoneDebugTask(this), 0L, 22L).getTaskId();

        floorFlagTime = 0;

        if (lobbyLocation.getWorld() != null) clearOldBalls(lobbyLocation.getWorld());

        badmintonBall = new BadmintonBall(this);

        // Check if the game is 1 vs 1 or 2 vs 2 using the team sizes
        if (teamHelper.getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 1)) {
            // 1 vs 1
            setGameType(BadmintonGameType.SINGLES);
        } else {
            // 2 vs 2
            setGameType(BadmintonGameType.DOUBLES);
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<IBadmintonPlayer> badmintonPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (badmintonPlayerOptional.isEmpty()) {
                player.setAllowFlight(true);
                player.sendMessage(CommonUtil.colorize("&a[!] You are now spectating the match! You can fly!"));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0F);
            }
        });

        playerHelper.getPlayerList().forEach(participant -> {
            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            final IBadmintonTeam team = participant.getTeam();
            if (team == null) return;

            // Make sure the player is not flying
            player.setAllowFlight(false);

            // Get a random spawn
            player.teleport(team.getStandardSpawns().get(new Random().nextInt(team.getStandardSpawns().size())));
        });

        // Announce the game start, and the subtitle must be player1 vs player2, or player1 & player2 vs player3 & player4
        final Set<IBadmintonPlayer> team1Players = teamHelper.getTeamList().get(0).getPlayers();
        final Set<IBadmintonPlayer> team2Players = teamHelper.getTeamList().get(1).getPlayers();

        getArenaAnnouncer().sendGlobalTitle(
                "&6&lBADMINTON",
                "&7" + team1Players.stream().map(IBadmintonPlayer::getName).collect(Collectors.joining(" &f&&7 ")) + " &fvs&7 " + team2Players.stream().map(IBadmintonPlayer::getName).collect(Collectors.joining(" &f&&7 ")),
                0,
                40,
                20);

        getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F);

        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getArenaAnnouncer().sendGlobalCenteredMessage("&f&lBadminton");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lThrow the ball to the other side to score a point.");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lThe first team to reach 10 points wins the game!");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        // Select a random team and set the first service
        final IBadmintonTeam firstServiceTeam = teamHelper.getTeamList().get(new Random().nextInt(teamHelper.getTeamList().size()));
        final ChatColor firstServiceTeamColor = ChatColor.valueOf(firstServiceTeam.getColor());

        getArenaAnnouncer().sendGlobalMessage("&e[!] " + firstServiceTeamColor + firstServiceTeam.getPlayers().stream().map(IBadmintonPlayer::getName).collect(Collectors.joining(" &eand " + firstServiceTeamColor)) + " &ewill serve first.");
        Bukkit.getScheduler().runTaskLater(
                getBadmintonMiniGame().getHoloSportsGame(),
                new ShuttecockSpawnTask(this, firstServiceTeam),
                20 * 2
        );

        Bukkit.getScheduler().cancelTask(badmintonMatchProperties.getStartingTaskId());

        setState(BadmintonMatchState.GAME);
    }

    public void stop() {
        setState(BadmintonMatchState.ENDING);

        stopTasks();

        if (badmintonBall != null) {
            badmintonBall.deSpawn();
            badmintonBall = null;
        }

        if (lobbyLocation.getWorld() != null) clearOldBalls(lobbyLocation.getWorld());

        final IBadmintonPlayerHelper playerHelper = badmintonMiniGame.getPlayerHelper();
        final IBadmintonTeamHelper teamHelper = badmintonMiniGame.getTeamHelper();

        final Optional<IBadmintonTeam> winnerTeamOptional = teamHelper.getTeamWithMostPoints();

        if (winnerTeamOptional.isEmpty()) {
            getArenaAnnouncer().sendGlobalMessage(" \n&6&lThe game ended with a TIE!, this should not be happening?");
            getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_CAT_PURREOW, 0.8F, 0.8F);
        } else {
            final IBadmintonTeam winnerTeam = winnerTeamOptional.get();
            final ChatColor winnerTeamColor = ChatColor.valueOf(winnerTeam.getColor());
            getArenaAnnouncer().sendGlobalTitle(
                    "&aGame ended!",
                    winnerTeamColor + winnerTeam.getPlayers().stream().map(IBadmintonPlayer::getName).collect(Collectors.joining(" &f& " + winnerTeamColor)) + winnerTeamColor + " &7won the match!",
                    0,
                    100,
                    20);

            final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
            for (final IBadmintonTeam soccerTeam : teamHelper.getTeamList()) {
                teamScoreBuilder
                        .append(ChatColor.valueOf(soccerTeam.getColor()))
                        .append(soccerTeam.getPoints())
                        .append(" &f- ");
            }
            // Remove the last 5 characters (space, dash, space, bracket, space)
            final String score = teamScoreBuilder.substring(0, teamScoreBuilder.length() - 5) + "&f] ";

            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            getArenaAnnouncer().sendGlobalCenteredMessage("&e&lMatch ended!");
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&eWinner team: " + ChatColor.valueOf(winnerTeam.getColor()) + winnerTeam.getName());
            getArenaAnnouncer().sendGlobalCenteredMessage("&7" + winnerTeam.getPlayers().stream().map(IBadmintonPlayer::getName).collect(Collectors.joining(" &f&&7 ")));
            getArenaAnnouncer().sendGlobalCenteredMessage(score);
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.8F));
            fireworkTaskId = Bukkit.getScheduler().runTaskTimer(badmintonMiniGame.getHoloSportsGame(), new MatchWinnerFireworksTask(this, winnerTeam), 0L, 20L).getTaskId();
        }

        // CLear team data
        Bukkit.getScheduler().runTaskLater(badmintonMiniGame.getHoloSportsGame(), () -> {
            playerHelper.getPlayerList().forEach(badmintonPlayer -> {
                BadmintonGameItemCollection.resetPlayerHotbar(badmintonPlayer);

                final IBadmintonTeam team = badmintonPlayer.getTeam();

                if (team != null) teamHelper.removePlayerFromTeam(badmintonPlayer, team.getIdentifier());

                playerHelper.removePlayer(badmintonPlayer.getUniqueId());
            });

            // Get all teams, clear all the players if there are and set the goals to 0
            teamHelper.getTeamList().forEach(team -> {
                if (team.getPlayers().size() != 0) team.getPlayers().forEach(teamPlayer -> teamHelper.removePlayerFromTeam(teamPlayer, team.getIdentifier()));
                team.setPoints(0);
            });

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setAllowFlight(false);
                player.setFlying(false);

                player.teleport(lobbyLocation);
                player.setGameMode(GameMode.ADVENTURE);

                if (NicePlayersUtil.isNicePlayer(player)) {
                    final Inventory inventory = player.getInventory();

                    inventory.setItem(5, BadmintonGameItemCollection.getStartItem());
                    inventory.setItem(6, BadmintonGameItemCollection.getStopItem());
                }
            });

            // Reset starting properties
            getBadmintonMatchProperties().resetTimer();

            setState(BadmintonMatchState.WAITING);

            Bukkit.getScheduler().cancelTask(fireworkTaskId);
        }, 120L);
    }

    private void stopTasks() {
        Bukkit.getScheduler().cancelTask(arenaTickTask);
        arenaTickTask = -1;
        Bukkit.getScheduler().cancelTask(ballTrailTask);
        ballTrailTask = -1;
        Bukkit.getScheduler().cancelTask(ballParticleDebugTask);
        ballParticleDebugTask = -1;
        Bukkit.getScheduler().cancelTask(arenaActionBarTask);
        arenaActionBarTask = -1;

        if (ballLandTask != -1) Bukkit.getScheduler().cancelTask(ballLandTask);
    }

    public void teamService(final IBadmintonTeam team) {
        lastServiceTeam = team;

        if (team.getPlayers().size() == 0) {
            getArenaAnnouncer().sendGlobalMessage("&c&lTeam " + team.getName() + " has no players left!");
            stop();
            return;
        }
        // Get a random member of the team and teleport them near the shuttle
        final IBadmintonPlayer randomPlayer = team.getPlayers().stream().toList().get(new Random().nextInt(team.getPlayers().size()));
        final Player player = Bukkit.getPlayer(randomPlayer.getUniqueId());
        if (player == null) return;

        spawnShuttlecock(team, getScoreType(team));

        if (badmintonBall == null) return;

        if (badmintonBall.getBallArmorStand() != null) {
            final ArmorStand armorStand = badmintonBall.getBallArmorStand().getArmorStand();
            if (armorStand == null) return;

            // IF the player team is white, plus x, otherwise minus x
            final double x = team.getColor().equals("WHITE") ? 2.0 : -2.0;
            player.teleport(armorStand.getLocation().clone().add(x, -0.5, 0));
        }
    }

    public ScoreType getScoreType(final IBadmintonTeam team) {
        final int score = team.getPoints();

        return score % 2 == 0 ? ScoreType.EVEN : ScoreType.ODD;
    }

    public void spawnShuttlecock(final IBadmintonTeam serviceTeam, final ScoreType scoreType) {
        switch (scoreType) {
            case EVEN -> {
                final Location location = serviceTeam.getShuttlecockSpawnEven();
                if (badmintonBall != null) {
                    badmintonBall.deSpawn();
                    badmintonBall.spawn(location);

                    badmintonBall.setLastTagger(null);
                }

                // Spawn arrows
                final Location arrowsLocation = serviceTeam.getShuttlecockSpawnOdd();
                final SideArrowIndicatorTask arrowIndicatorTask = new SideArrowIndicatorTask(this, arrowsLocation);
                sideArrowIndicatorTasks.add(arrowIndicatorTask);

                // Spawn arrows for the other team
                final Location arrowsLocation2 = teamHelper.getOppositeTeam(serviceTeam).getShuttlecockSpawnOdd();
                final SideArrowIndicatorTask arrowIndicatorTask2 = new SideArrowIndicatorTask(this, arrowsLocation2);
                sideArrowIndicatorTasks.add(arrowIndicatorTask2);
            }
            case ODD -> {
                final Location location = serviceTeam.getShuttlecockSpawnOdd();
                if (badmintonBall != null) {
                    badmintonBall.deSpawn();
                    badmintonBall.spawn(location);

                    badmintonBall.setLastTagger(null);
                }

                // Spawn arrows
                final Location arrowsLocation = serviceTeam.getShuttlecockSpawnEven();
                final SideArrowIndicatorTask arrowIndicatorTask = new SideArrowIndicatorTask(this, arrowsLocation);
                sideArrowIndicatorTasks.add(arrowIndicatorTask);

                // Spawn arrows for the other team
                final Location arrowsLocation2 = teamHelper.getOppositeTeam(serviceTeam).getShuttlecockSpawnEven();
                final SideArrowIndicatorTask arrowIndicatorTask2 = new SideArrowIndicatorTask(this, arrowsLocation2);
                sideArrowIndicatorTasks.add(arrowIndicatorTask2);
            }
        }
    }

    public void shootShuttlecock(final IBadmintonPlayer badmintonPlayer, final BadmintonBall badmintonBall, final Location targetLocation, final boolean smash, double height) {
        final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());
        if (player == null) return;

        if (badmintonPlayer.getTeam() == null) return;

        badmintonBall.setLastTagger(badmintonPlayer);

        final ArmorStand armorStand = badmintonBall.getBallArmorStand().getArmorStand();
        if (armorStand == null) return;

        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1.5F, 1.6F);

        final Slime slime = badmintonBall.getBallArmorStand().getSlime();
        if (slime == null) return;

        slime.setGravity(true);
        slime.setCollidable(true);

        final World world = slime.getWorld();

        FallingBlock fallingBlock;
        if (badmintonBall.getBallArmorStand().getFallingBlock() == null || badmintonBall.getBallArmorStand().getFallingBlock().isDead()) {
            final FallingBlock fb = world.spawnFallingBlock(slime.getLocation(), Material.MOVING_PISTON.createBlockData());
            fb.setDropItem(false);

            fallingBlock = fb;
            badmintonBall.getBallArmorStand().setFallingBlock(fallingBlock);
        } else {
            fallingBlock = badmintonBall.getBallArmorStand().getFallingBlock();
        }

        // Clear the indicators
        sideArrowIndicatorTasks.forEach(task -> Bukkit.getScheduler().runTask(badmintonMiniGame.getHoloSportsGame(), task));

        fallingBlock.teleport(fallingBlock.getLocation().add(0, 0.2, 0));

        badmintonBall.setFlying(true);

        final Location startLocation = fallingBlock.getLocation();
        startLocation.setDirection(new Vector(0, 0, 0));

        final Vector selectedTarget = targetLocation.toVector();

        double acceleration = 0.08;

        double drag = 0.02;
        double inertia = 0.91;

        double startHeight = 0;
        double startVelocity = 0;

        if (smash) {
            height = 1.8;
            startVelocity += 0.15;
            acceleration += 0.03;

            badmintonBall.setSmashed(true);

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.4F, 1.4F);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.2F, 1.2F);
            world.spawnParticle(Particle.SMALL_FLAME, slime.getLocation(), 100, 0.5, 0.5, 0.5, 0.01);
        } else {
            badmintonBall.setSmashed(false);
        }

        int startTicks = 0;

        while (startHeight < height) {
            startTicks++;
            startVelocity = startVelocity / (1 - drag) + acceleration;
            startHeight += startVelocity;
        }

        double endHeight = selectedTarget.getY() + startHeight;
        double endVelocity = 0.0;
        int endTicks = 0;

        final double targetLocationHeight = targetLocation.getY();

        while (endHeight > targetLocationHeight) {
            endTicks++;
            endVelocity = (endVelocity + acceleration) * (1 - drag); //not quite sure about order
            endHeight -= endVelocity;
        }

        final Vector flatEntityLocation = startLocation.toVector().clone().setY(0);
        final Vector flatTargetLocation = selectedTarget.clone();
        flatTargetLocation.setY(0);

        final double distance = flatEntityLocation.clone().setY(0).distance(flatTargetLocation.clone().setY(0));
        final double flatVelocity = ((inertia - 1) * distance) / (Math.pow(inertia, startTicks + endTicks - 1) - 1);
        final Vector flatVelocityVector = flatTargetLocation.clone().subtract(flatEntityLocation).normalize().multiply(flatVelocity);

        final Vector flingVelocity = flatVelocityVector.clone().setY(startVelocity);

        fallingBlock.setVelocity(flingVelocity);

        armorStand.setHeadPose(new EulerAngle(Math.toRadians(targetLocation.getPitch()), Math.toRadians(targetLocation.getYaw()), 0));

        if (ballLandTask != -1) {
            Bukkit.getScheduler().cancelTask(ballLandTask);
        }

        startShootTask(armorStand, slime, fallingBlock);
    }

    public void startShootTask(final ArmorStand armorStand, final Slime slime, final FallingBlock fallingBlock) {
        if (ballLandTask != -1) {
            floorFlagTime = 0;
            Bukkit.getScheduler().cancelTask(ballLandTask);
        }

        //System.out.println("======== Starting shoot task ========");
        ballLandTask = Bukkit.getScheduler().runTaskTimer(HoloSportsGame.getHoloSportsGame(), () -> {
            boolean floor = slime.isOnGround();
            // Check the Y distance between the floor and the ball
            final double tolerance = slime.getLocation().getY() - slime.getWorld().getHighestBlockYAt(slime.getLocation());
            //System.out.println(tolerance);

            if (!floor && tolerance < 1.65) floor = true;

            if (floor) {
                floorFlagTime++;

                //System.out.println(floorFlagTime + " / " + badmintonMatchProperties.getFloorToleranceTime());

                if (floorFlagTime != badmintonMatchProperties.getFloorToleranceTime()) {
                    return;
                }

                if (badmintonBall == null) return;

                final IBadmintonTeam team = badmintonBall.getLastTagger().getTeam();
                if (team == null) return;

                fallingBlock.remove();
                badmintonBall.getBallArmorStand().setFallingBlock(null);

                badmintonBall.setFlying(false);

                final Cuboid cuboid = getGoalZone(team);
                if (cuboid == null) return;

                final IBadmintonTeam firstTeam = teamHelper.getTeamList().get(0);
                final int firstTeamPoints = firstTeam.getPoints();
                final IBadmintonTeam secondTeam = teamHelper.getTeamList().get(1);
                final int secondTeamPoints = secondTeam.getPoints();

                final IBadmintonPlayer lastTagger = badmintonBall.getLastTagger();

                final String format = "%s&l%s&r &f&l- %s&l%s";
                final String subFormat = "&a[%s] &fscored for %sTeam %s";
                final String subFormatOut = "&a[%s] &fhit the ball out!";

                IBadmintonTeam serverTeam;

                if (cuboid.containsLocation(fallingBlock.getLocation())) {
                    team.setPoints(team.getPoints() + 1);
                    serverTeam = team;

                    if (team.getPoints() == 10) {
                        stop();
                        return;
                    }

                    getArenaAnnouncer().sendGlobalTitle(
                            String.format(format, ChatColor.valueOf(firstTeam.getColor()), firstTeam.getPoints(), ChatColor.valueOf(secondTeam.getColor()), secondTeam.getPoints()),
                            String.format(subFormat, lastTagger.getName(), ChatColor.valueOf(badmintonBall.getLastTagger().getTeam().getColor()), badmintonBall.getLastTagger().getTeam().getName()),
                            0,
                            15,
                            0
                    );

                    Bukkit.getScheduler().runTaskLater(getBadmintonMiniGame().getHoloSportsGame(), () -> {
                        // CHeck the team that changed points
                        if (firstTeam.getPoints() > firstTeamPoints) {
                            getArenaAnnouncer().sendGlobalTitle(
                                    String.format(format, ChatColor.valueOf(firstTeam.getColor()) + String.valueOf(ChatColor.UNDERLINE), firstTeam.getPoints(), ChatColor.DARK_GRAY, secondTeam.getPoints()),
                                    String.format(subFormat, lastTagger.getName(), ChatColor.valueOf(lastTagger.getTeam().getColor()), lastTagger.getTeam().getName()),
                                    0,
                                    40,
                                    10
                            );
                        } else if (secondTeam.getPoints() > secondTeamPoints) {
                            getArenaAnnouncer().sendGlobalTitle(
                                    String.format(format, ChatColor.DARK_GRAY, firstTeam.getPoints(), ChatColor.valueOf(secondTeam.getColor()) + String.valueOf(ChatColor.UNDERLINE), secondTeam.getPoints()),
                                    String.format(subFormat, lastTagger.getName(), ChatColor.valueOf(lastTagger.getTeam().getColor()), lastTagger.getTeam().getName()),
                                    0,
                                    40,
                                    10
                            );
                        }
                    }, 10);
                } else {
                    final IBadmintonTeam oppositeTeam = teamHelper.getOppositeTeam(team);
                    if (oppositeTeam == null) return;
                    serverTeam = oppositeTeam;

                    oppositeTeam.setPoints(oppositeTeam.getPoints() + 1);

                    if (oppositeTeam.getPoints() == 10) {
                        stop();
                        return;
                    }

                    getArenaAnnouncer().sendGlobalTitle(
                            String.format(format, ChatColor.valueOf(firstTeam.getColor()), firstTeam.getPoints(), ChatColor.valueOf(secondTeam.getColor()), secondTeam.getPoints()),
                            String.format(subFormatOut, lastTagger.getName()),
                            0,
                            15,
                            0
                    );

                    Bukkit.getScheduler().runTaskLater(getBadmintonMiniGame().getHoloSportsGame(), () -> {
                        // CHeck the team that changed points
                        if (firstTeam.getPoints() > firstTeamPoints) {
                            getArenaAnnouncer().sendGlobalTitle(
                                    String.format(format, ChatColor.valueOf(firstTeam.getColor()) + String.valueOf(ChatColor.UNDERLINE), firstTeam.getPoints(), ChatColor.DARK_GRAY, secondTeam.getPoints()),
                                    String.format(subFormatOut, lastTagger.getName()),
                                    0,
                                    40,
                                    10
                            );
                        } else if (secondTeam.getPoints() > secondTeamPoints) {
                            getArenaAnnouncer().sendGlobalTitle(
                                    String.format(format, ChatColor.DARK_GRAY, firstTeam.getPoints(), ChatColor.valueOf(secondTeam.getColor()) + String.valueOf(ChatColor.UNDERLINE), secondTeam.getPoints()),
                                    String.format(subFormatOut, lastTagger.getName()),
                                    0,
                                    40,
                                    10
                            );
                        }
                    }, 10);
                }

                getArenaAnnouncer().sendGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.1F, 1.1F);
                if (badmintonBall != null) badmintonBall.deSpawn();

                Bukkit.getScheduler().cancelTask(ballLandTask);
                ballLandTask = -1;
                floorFlagTime = 0;

                Bukkit.getScheduler().runTask(HoloSportsGame.getHoloSportsGame(), new DownArrowIndicatorTask(this, armorStand.getLocation()));

                Bukkit.getScheduler().runTaskLater(
                        getBadmintonMiniGame().getHoloSportsGame(),
                        new ShuttecockSpawnTask(this, serverTeam),
                        20 * 3
                );
            }
        }, 4L, 1L).getTaskId();
    }

    public Cuboid getGoalZone(final IBadmintonTeam taggerTeam) {
        final ScoreType scoreType = getScoreType(taggerTeam);
        if (scoreType == null) return null;
        final HitType hitType = badmintonBall.getHitType();
        if (hitType == null) return null;

        Cuboid goalCuboid = null;

        switch (gameType) {
            case DOUBLES -> {
                switch (hitType) {
                    case SERVICE -> { // La pelota cayó gracias al team que sacó primero
                        if (scoreType == ScoreType.EVEN) {
                            goalCuboid = taggerTeam.getDoubleServePointZoneEven();
                        }
                        if (scoreType == ScoreType.ODD) {
                            goalCuboid = taggerTeam.getDoubleServePointZoneOdd();
                        }
                    }
                    case GAME -> { // La pelota cayó tras varios intercambios
                        goalCuboid = taggerTeam.getDoublePointZone();
                    }
                }
            }
            case SINGLES -> {
                switch (hitType) {
                    case SERVICE -> { // La pelota cayó gracias al team que sacó primero
                        if (scoreType == ScoreType.EVEN) {
                            goalCuboid = taggerTeam.getSingleServePointZoneEven();
                        }
                        if (scoreType == ScoreType.ODD) {
                            goalCuboid = taggerTeam.getSingleServePointZoneOdd();
                        }
                    }
                    case GAME -> { // La pelota cayó tras varios intercambios
                        goalCuboid = taggerTeam.getSinglePointZone();
                    }
                }
            }
        }

        return goalCuboid;
    }

    private void clearOldBalls(final World world) {
        world.getEntities().stream().filter(entity -> entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.SLIME).forEach(entity -> {
            final boolean hasNbt = NBTMetadataUtil.hasEntityString(entity, "accessor");

            if (!hasNbt) return;

            if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("game_ball")) {
                HoloSportsGame.getHoloSportsGame().getLogger().info("Removing old shuttlecock: " + entity.getUniqueId());
                entity.remove();
            }

            if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("hitbox_game_ball")) {
                HoloSportsGame.getHoloSportsGame().getLogger().info("Removing old hitbox slime: " + entity.getUniqueId());
                entity.remove();
            }

            if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("badminton_arrow")) {
                HoloSportsGame.getHoloSportsGame().getLogger().info("Removing old arrow: " + entity.getUniqueId());
                entity.remove();
            }
        });
    }
}
