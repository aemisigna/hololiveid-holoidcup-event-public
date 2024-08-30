package com.covercorp.holosports.game.minigame.tug.arena;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.arena.bar.TimeBarHelper;
import com.covercorp.holosports.game.minigame.tug.arena.command.*;
import com.covercorp.holosports.game.minigame.tug.arena.listener.MatchGameListener;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.arena.task.*;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.arena.inventory.TugGameItemCollection;
import com.covercorp.holosports.game.minigame.tug.arena.task.MatchWinnerFireworksTask;
import com.covercorp.holosports.game.minigame.tug.player.ITugPlayerHelper;
import com.covercorp.holosports.game.minigame.tug.team.ITugTeamHelper;
import com.covercorp.holosports.game.minigame.tug.arena.listener.MatchListener;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.announcer.ArenaAnnouncer;
import com.covercorp.holosports.game.minigame.tug.arena.properties.TugMatchProperties;
import com.covercorp.holosports.game.util.NicePlayersUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter(AccessLevel.PUBLIC)
public final class TugArena {
    private final TugMiniGame tugMiniGame;
    private final ITugPlayerHelper playerHelper;
    private final ITugTeamHelper teamHelper;

    private final TimeBarHelper timeBarHelper;
    
    private final TugMatchProperties tugMatchProperties;
    private final Location lobbyLocation;
    private final Location centerLocation;

    private final ArenaAnnouncer arenaAnnouncer;

    @Setter(AccessLevel.PUBLIC) private TugMatchState state;

    @Setter(AccessLevel.PUBLIC) private int gameTime = 300;

    private int arenaTimeTask = -1;
    private int arenaActionBarTask = -1;

    private int fireworkTaskId = -1;

    public TugArena(final TugMiniGame tugMiniGame) {
        this.tugMiniGame = tugMiniGame;

        playerHelper = tugMiniGame.getPlayerHelper();
        teamHelper = tugMiniGame.getTeamHelper();

        timeBarHelper = new TimeBarHelper(this);

        tugMatchProperties = new TugMatchProperties(this);

        arenaAnnouncer = new ArenaAnnouncer(getPlayerHelper());

        lobbyLocation = tugMiniGame.getTugConfigHelper().getLobbySpawn();
        centerLocation = tugMiniGame.getTugConfigHelper().getRopeCenter();

        state = TugMatchState.WAITING;

        // Listeners
        Bukkit.getPluginManager().registerEvents(new MatchListener(tugMiniGame, this), tugMiniGame.getHoloSportsGame());
        Bukkit.getPluginManager().registerEvents(new MatchGameListener(tugMiniGame, this), tugMiniGame.getHoloSportsGame());

        // COMMANDS
        new AddPlayerToTeamCommand(this).register(tugMiniGame.getHoloSportsGame());
        new RemovePlayerToTeamCommand(this).register(tugMiniGame.getHoloSportsGame());
        new ClearTeamPlayersCommand(this).register(tugMiniGame.getHoloSportsGame());

        // GLOBAL COMMANDS
        new StartMatchCommand(this).register(tugMiniGame.getHoloSportsGame());
        new StopMatchCommand(this).register(tugMiniGame.getHoloSportsGame());
    }

    public void start() {
        Bukkit.getScheduler().cancelTask(tugMatchProperties.getStartingTaskId());

        setGameTime(300);

        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<ITugPlayer> tugPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (tugPlayerOptional.isEmpty()) {
                player.sendMessage(CommonUtil.colorize("&a[!] You are now spectating the match! You can fly!"));
                player.setAllowFlight(true);
            }
        });

        playerHelper.getPlayerList().forEach(participant -> {
            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            final ITugTeam team = participant.getTeam();
            if (team == null) return;

            player.setAllowFlight(false);

            // Get a random spawn
            player.teleport(team.getSpawn());
        });

        // Announce the game start, and the subtitle must be player1 vs player2, or player1 & player2 vs player3 & player4
        final Set<ITugPlayer> team1Players = teamHelper.getTeamList().get(0).getPlayers();
        final Set<ITugPlayer> team2Players = teamHelper.getTeamList().get(1).getPlayers();

        getArenaAnnouncer().sendGlobalTitle(
                "&6&lTUG OF WAR",
                "&7" + team1Players.stream().map(ITugPlayer::getName).collect(Collectors.joining(" &f&&7 ")) + " &fvs&7 " + team2Players.stream().map(ITugPlayer::getName).collect(Collectors.joining(" &f&&7 ")),
                0,
                40,
                20);

        getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F);

        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getArenaAnnouncer().sendGlobalCenteredMessage("&f&lTug of War");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lClick the levers to pull the rope!");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lThe first team to reach 100 points of difference");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lor get more points at the time up will win!");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        getTugMatchProperties().setRopeStarting(true);
        getTugMatchProperties().setRopeStartingTaskId(Bukkit.getScheduler().runTaskTimer(tugMiniGame.getHoloSportsGame(), new RopeEnableTask(this), 0L, 20L).getTaskId());

        setState(TugMatchState.ROPE_STARTING);
    }

    public void enableRopes() {
        Bukkit.getScheduler().cancelTask(tugMatchProperties.getRopeStartingTaskId());

        arenaTimeTask = Bukkit.getScheduler().runTaskTimer(tugMiniGame.getHoloSportsGame(), new TugTimeTask(this), 0L, 20L).getTaskId();
        arenaActionBarTask = Bukkit.getScheduler().runTaskTimer(tugMiniGame.getHoloSportsGame(), new MatchActionBarTask(tugMiniGame, this), 0L, 20L).getTaskId();

        timeBarHelper.start();

        getArenaAnnouncer().sendGlobalMessage("&eThe levers are now enabled! Pull the rope!");
        getArenaAnnouncer().sendGlobalTitle("&a&lPULL!", "", 0, 20, 10);
        getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0F, 2.0F);

        setState(TugMatchState.GAME);
    }

    public void runLoser() {
        setState(TugMatchState.ENDING);

        final ITugTeam team1 = teamHelper.getTeamList().get(0);
        final Set<ITugPlayer> team1Players = team1.getPlayers();
        int team1AlivePlayers = 0;
        for (final ITugPlayer tugPlayer : team1Players) {
            if (!tugPlayer.isSpectating()) team1AlivePlayers++;
        }

        final ITugTeam team2 = teamHelper.getTeamList().get(1);
        final Set<ITugPlayer> team2Players = team2.getPlayers();
        int team2AlivePlayers = 0;
        for (final ITugPlayer tugPlayer : team2Players) {
            if (!tugPlayer.isSpectating()) team2AlivePlayers++;
        }

        // Check if some team has 0 players alive
        if (team1AlivePlayers == 0) {
            team2.setPoints(team2.getPoints() + 888);
            getArenaAnnouncer().sendGlobalMessage("&6Team " + team2.getName() + " has won because team " + team1.getName() + " has no players alive!");
        } else if (team2AlivePlayers == 0) {
            team1.setPoints(team1.getPoints() + 888);
            getArenaAnnouncer().sendGlobalMessage("&6Team " + team1.getName() + " has won because team " + team2.getName() + " has no players alive!");
        } else {
            if (team1.getPoints() == team2.getPoints()) {
                if (team1AlivePlayers > team2AlivePlayers) {
                    team1.setPoints(team1.getPoints() + 999);
                    getArenaAnnouncer().sendGlobalMessage("&6Team " + team1.getName() + " has won because team " + team2.getName() + " has no players alive!");
                } else {
                    team2.setPoints(team2.getPoints() + 999);
                    getArenaAnnouncer().sendGlobalMessage("&6Team " + team2.getName() + " has won because team " + team1.getName() + " has no players alive!");
                }
            }
        }

        final Optional<ITugTeam> winnerTeamOptional = teamHelper.getTeamWithMostPoints();
        if (winnerTeamOptional.isEmpty()) {
            getArenaAnnouncer().sendGlobalMessage(" \n&6&lThere's no winner team? This should not be happening?");
            getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_CAT_PURREOW, 0.8F, 0.8F);

            stopTasks();

            timeBarHelper.stop();

            Bukkit.getScheduler().runTaskLater(tugMiniGame.getHoloSportsGame(), () -> {
                playerHelper.getPlayerList().forEach(tugPlayer -> {
                    TugGameItemCollection.resetPlayerHotbar(tugPlayer);

                    final ITugTeam team = tugPlayer.getTeam();

                    if (team != null) teamHelper.removePlayerFromTeam(tugPlayer, team.getIdentifier());

                    playerHelper.removePlayer(tugPlayer.getUniqueId());
                });

                // Get all teams, clear all the players if there are and set the goals to 0
                teamHelper.getTeamList().forEach(team -> {
                    if (team.getPlayers().size() != 0) team.getPlayers().forEach(teamPlayer -> teamHelper.removePlayerFromTeam(teamPlayer, team.getIdentifier()));
                    team.setPoints(0);
                });

                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.getInventory().clear();

                    player.setAllowFlight(false);
                    player.setFlying(false);

                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(lobbyLocation);

                    if (NicePlayersUtil.isNicePlayer(player)) {
                        final Inventory inventory = player.getInventory();

                        inventory.setItem(5, TugGameItemCollection.getStartItem());
                        inventory.setItem(6, TugGameItemCollection.getStopItem());
                    }
                });

                // Reset starting properties
                getTugMatchProperties().resetTimer();

                setState(TugMatchState.WAITING);
            }, 120L);
            return;
        }

        final ITugTeam winnerTeam = winnerTeamOptional.get();
        final ITugTeam loserTeam = teamHelper.getOppositeTeam(winnerTeam);
        if (loserTeam == null) {
            getArenaAnnouncer().sendGlobalMessage(" \n&6&lThere's no loser team? This should not be happening?");
            getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_CAT_PURREOW, 0.8F, 0.8F);

            stopTasks();

            timeBarHelper.stop();

            Bukkit.getScheduler().runTaskLater(tugMiniGame.getHoloSportsGame(), () -> {
                playerHelper.getPlayerList().forEach(tugPlayer -> {
                    final Player player = Bukkit.getPlayer(tugPlayer.getUniqueId());
                    if (player == null) return;

                    player.teleport(lobbyLocation);

                    TugGameItemCollection.resetPlayerHotbar(tugPlayer);

                    final ITugTeam team = tugPlayer.getTeam();

                    if (team != null) teamHelper.removePlayerFromTeam(tugPlayer, team.getIdentifier());

                    playerHelper.removePlayer(tugPlayer.getUniqueId());
                });

                // Get all teams, clear all the players if there are and set the goals to 0
                teamHelper.getTeamList().forEach(team -> {
                    if (team.getPlayers().size() != 0) team.getPlayers().forEach(teamPlayer -> teamHelper.removePlayerFromTeam(teamPlayer, team.getIdentifier()));
                    team.setPoints(0);
                });

                // Reset starting properties
                getTugMatchProperties().resetTimer();

                setState(TugMatchState.WAITING);
            }, 120L);

            return;
        }

        Bukkit.getScheduler().runTask(getTugMiniGame().getHoloSportsGame(), new MatchLoserKillTask(this, loserTeam));
    }

    public void stop() {
        setState(TugMatchState.ENDING);

        stopTasks();

        timeBarHelper.stop();

        final ITugPlayerHelper playerHelper = tugMiniGame.getPlayerHelper();
        final ITugTeamHelper teamHelper = tugMiniGame.getTeamHelper();

        final Optional<ITugTeam> winnerTeamOptional = teamHelper.getTeamWithMostPoints();

        if (winnerTeamOptional.isEmpty()) {
            getArenaAnnouncer().sendGlobalMessage(" \n&6&lThe game ended with a TIE!, this should not be happening?");
            getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_CAT_PURREOW, 0.8F, 0.8F);
        } else {
            final ITugTeam winnerTeam = winnerTeamOptional.get();
            final ChatColor winnerTeamColor = ChatColor.valueOf(winnerTeam.getColor());
            getArenaAnnouncer().sendGlobalTitle(
                    "&aGame ended!",
                    winnerTeamColor + winnerTeam.getPlayers().stream().map(ITugPlayer::getName).collect(Collectors.joining(" &f& " + winnerTeamColor)) + " &7won the match!",
                    0,
                    100,
                    20);

            final StringBuilder teamScoreBuilder = new StringBuilder("&f[");
            for (final ITugTeam soccerTeam : teamHelper.getTeamList()) {
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
            getArenaAnnouncer().sendGlobalCenteredMessage("&7" + winnerTeam.getPlayers().stream().map(ITugPlayer::getName).collect(Collectors.joining(" &f&&7 ")));
            getArenaAnnouncer().sendGlobalCenteredMessage(score);
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.8F));

            fireworkTaskId = Bukkit.getScheduler().runTaskTimer(tugMiniGame.getHoloSportsGame(), new MatchWinnerFireworksTask(this, winnerTeam), 0L, 20L).getTaskId();

            Bukkit.getScheduler().runTaskLater(tugMiniGame.getHoloSportsGame(), () -> {
                Bukkit.getScheduler().cancelTask(fireworkTaskId);
                fireworkTaskId = -1;
            }, 20L * 3L);
        }

        // CLear team data
        Bukkit.getScheduler().runTaskLater(tugMiniGame.getHoloSportsGame(), () -> {
            playerHelper.getPlayerList().forEach(tugPlayer -> {
                TugGameItemCollection.resetPlayerHotbar(tugPlayer);

                final ITugTeam team = tugPlayer.getTeam();

                if (team != null) teamHelper.removePlayerFromTeam(tugPlayer, team.getIdentifier());

                playerHelper.removePlayer(tugPlayer.getUniqueId());
            });

            // Get all teams, clear all the players if there are and set the goals to 0
            teamHelper.getTeamList().forEach(team -> {
                if (team.getPlayers().size() != 0) team.getPlayers().forEach(teamPlayer -> teamHelper.removePlayerFromTeam(teamPlayer, team.getIdentifier()));
                team.setPoints(0);
            });

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.getInventory().clear();

                player.setAllowFlight(false);
                player.setFlying(false);

                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(lobbyLocation);

                if (NicePlayersUtil.isNicePlayer(player)) {
                    final Inventory inventory = player.getInventory();

                    inventory.setItem(5, TugGameItemCollection.getStartItem());
                    inventory.setItem(6, TugGameItemCollection.getStopItem());
                }
            });

            // Reset starting properties
            getTugMatchProperties().resetTimer();

            setState(TugMatchState.WAITING);
        }, 120L);
    }

    private void stopTasks() {
        Bukkit.getScheduler().cancelTask(arenaTimeTask);
        arenaTimeTask = -1;
        Bukkit.getScheduler().cancelTask(arenaActionBarTask);
        arenaActionBarTask = -1;
        Bukkit.getScheduler().cancelTask(tugMatchProperties.getStartingTaskId());
        Bukkit.getScheduler().cancelTask(tugMatchProperties.getRopeStartingTaskId());
    }
}
