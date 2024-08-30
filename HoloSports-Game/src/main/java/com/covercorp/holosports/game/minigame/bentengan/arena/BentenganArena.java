package com.covercorp.holosports.game.minigame.bentengan.arena;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.command.*;
import com.covercorp.holosports.game.minigame.bentengan.arena.listener.BentenganMatchListener;
import com.covercorp.holosports.game.minigame.bentengan.arena.properties.BentenganMatchProperties;
import com.covercorp.holosports.game.minigame.bentengan.arena.task.BentenganTimeTask;
import com.covercorp.holosports.game.minigame.bentengan.arena.task.MatchActionBarTask;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.arena.task.BentenganTickTask;
import com.covercorp.holosports.game.minigame.bentengan.player.IBentenganPlayerHelper;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.IBentenganTeamHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import com.covercorp.holosports.game.minigame.bentengan.arena.announcer.ArenaAnnouncer;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.arena.task.MatchWinnerFireworksTask;

import com.covercorp.holosports.game.util.NicePlayersUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter(AccessLevel.PUBLIC)
public final class BentenganArena {
    private final BentenganMiniGame bentenganMiniGame;
    private final IBentenganPlayerHelper playerHelper;
    private final IBentenganTeamHelper teamHelper;

    private final BentenganMatchProperties bentenganMatchProperties;

    private final Location lobbyLocation;
    private final Cuboid midZone;

    private final ArenaAnnouncer arenaAnnouncer;

    @Setter(AccessLevel.PUBLIC) private BentenganMatchState state;

    @Setter(AccessLevel.PUBLIC) private @Nullable IBentenganTeam winnerTeam;

    @Setter(AccessLevel.PUBLIC) private int timeLimit = 600;

    private int arenaTickTask = -1;
    private int arenaTimeTask = -1;
    private int arenaActionBarTask = -1;

    private int fireworkTaskId = -1;

    public BentenganArena(final BentenganMiniGame bentenganMiniGame) {
        this.bentenganMiniGame = bentenganMiniGame;
        playerHelper = bentenganMiniGame.getPlayerHelper();
        teamHelper = bentenganMiniGame.getTeamHelper();

        bentenganMatchProperties = new BentenganMatchProperties(this);

        state = BentenganMatchState.WAITING;

        lobbyLocation = bentenganMiniGame.getBentenganConfigHelper().getLobbySpawn();
        midZone = bentenganMiniGame.getBentenganConfigHelper().getMidZone();

        arenaAnnouncer = new ArenaAnnouncer(playerHelper);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new BentenganMatchListener(this), bentenganMiniGame.getHoloSportsGame());

        // COMMANDS
        new AddPlayerToTeamCommand(this).register(bentenganMiniGame.getHoloSportsGame());
        new RemovePlayerToTeamCommand(this).register(bentenganMiniGame.getHoloSportsGame());
        new ClearTeamPlayersCommand(this).register(bentenganMiniGame.getHoloSportsGame());
        
        // GLOBAL COMMANDS
        new StartMatchCommand(this).register(bentenganMiniGame.getHoloSportsGame());
        new StopMatchCommand(this).register(bentenganMiniGame.getHoloSportsGame());
    }

    public void start() {
        Bukkit.getScheduler().cancelTask(bentenganMatchProperties.getStartingTaskId());

        setTimeLimit(600);
        setWinnerTeam(null);

        startTasks();

        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<IBentenganPlayer> bentenganPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (bentenganPlayerOptional.isEmpty()) {
                player.setAllowFlight(true);
                player.sendMessage(CommonUtil.colorize("&a[!] You are now spectating the match! You can fly!"));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0F);
            }
        });

        playerHelper.getPlayerList().forEach(participant -> {
            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            final IBentenganTeam team = participant.getTeam();
            if (team == null) return;

            BentenganGameItemCollection.setupPlayerHotbar(participant);

            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(team.getSpawnPoint());
        });

        getArenaAnnouncer().sendGlobalTitle(
                "&6&lBENTENGAN",
                "&7Good luck!",
                0,
                40,
                20
        );

        getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F);

        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getArenaAnnouncer().sendGlobalCenteredMessage("&f&lBentengan");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lReach the other team's beacon to win!");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lBeware of the other team members, if you get");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&ltouched in their half, you will be jailed!");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lYour teammates can free you by opening the iron");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&ldoor themselves.");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        state = BentenganMatchState.GAME;
    }

    public void stop() {
        state = BentenganMatchState.ENDING;

        stopTasks();

        final IBentenganPlayerHelper playerHelper = bentenganMiniGame.getPlayerHelper();
        final IBentenganTeamHelper teamHelper = bentenganMiniGame.getTeamHelper();

        final World world = lobbyLocation.getWorld();
        if (world != null) world.getLivingEntities().stream().filter(entity -> entity instanceof Mob).forEach(Entity::remove);

        if (winnerTeam != null) {
            final Set<IBentenganPlayer> teamPlayerList = winnerTeam.getPlayers();
            final ChatColor winnerTeamColor = ChatColor.valueOf(winnerTeam.getColor());

            getArenaAnnouncer().sendGlobalTitle(
                    "&aGame ended!",
                    winnerTeamColor + teamPlayerList.stream().map(IBentenganPlayer::getName).collect(Collectors.joining(" &f& " + winnerTeamColor)) + " &7won the match!",
                    0,
                    100,
                    20
            );

            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            getArenaAnnouncer().sendGlobalCenteredMessage("&e&lMatch ended!");
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&eWinner team: " + winnerTeamColor + winnerTeam.getName());
            getArenaAnnouncer().sendGlobalCenteredMessage("&7" + teamPlayerList.stream().map(IBentenganPlayer::getName).collect(Collectors.joining(" &f&&7 ")));
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            getArenaAnnouncer().sendGlobalSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.8F);

            fireworkTaskId = Bukkit.getScheduler().runTaskTimer(bentenganMiniGame.getHoloSportsGame(), new MatchWinnerFireworksTask(this, winnerTeam), 0L, 20L).getTaskId();

            Bukkit.getScheduler().runTaskLater(bentenganMiniGame.getHoloSportsGame(), () -> {
                Bukkit.getScheduler().cancelTask(fireworkTaskId);
                fireworkTaskId = -1;
            }, 20L * 3L);
        } else {
            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            getArenaAnnouncer().sendGlobalCenteredMessage("&e&lMatch ended in a DRAW!");
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&eWinner team: &7None");
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_CAT_PURREOW, 0.8F, 0.8F);
        }

        // Clear team data
        Bukkit.getScheduler().runTaskLater(bentenganMiniGame.getHoloSportsGame(), () -> {
            playerHelper.getPlayerList().forEach(bentenganPlayer -> {
                BentenganGameItemCollection.resetPlayerHotbar(bentenganPlayer);

                final IBentenganTeam team = bentenganPlayer.getTeam();
                if (team != null) teamHelper.removePlayerFromTeam(bentenganPlayer, team.getIdentifier());

                playerHelper.removePlayer(bentenganPlayer.getUniqueId());
            });

            teamHelper.getTeamList().forEach(team -> {
                if (team.getPlayers().size() != 0) {
                    team.getPlayers().forEach(teamPlayer -> teamHelper.removePlayerFromTeam(teamPlayer, team.getIdentifier()));
                }
            });

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.getInventory().clear();

                player.setAllowFlight(false);
                player.setFlying(false);

                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(lobbyLocation);

                if (NicePlayersUtil.isNicePlayer(player)) {
                    final Inventory inventory = player.getInventory();

                    inventory.setItem(5, BadmintonGameItemCollection.getStartItem());
                    inventory.setItem(6, BadmintonGameItemCollection.getStopItem());
                }
            });

            // Reset starting properties
            getBentenganMatchProperties().resetTimer();

            setState(BentenganMatchState.WAITING);
        }, 120L);
    }

    public void startTasks() {
        arenaTickTask = Bukkit.getScheduler().runTaskTimer(bentenganMiniGame.getHoloSportsGame(), new BentenganTickTask(this), 0L, 1L).getTaskId();
        arenaTimeTask = Bukkit.getScheduler().runTaskTimer(bentenganMiniGame.getHoloSportsGame(), new BentenganTimeTask(this), 0L, 20L).getTaskId();
        arenaActionBarTask = Bukkit.getScheduler().runTaskTimer(bentenganMiniGame.getHoloSportsGame(), new MatchActionBarTask(bentenganMiniGame, this), 0L, 20L).getTaskId();
    }

    private void stopTasks() {
        Bukkit.getScheduler().cancelTask(arenaTickTask);
        arenaTickTask = -1;
        Bukkit.getScheduler().cancelTask(arenaTimeTask);
        arenaTimeTask = -1;
        Bukkit.getScheduler().cancelTask(arenaActionBarTask);
        arenaActionBarTask = -1;
    }

    public void jailPlayer(final IBentenganPlayer participant) {
        final Player player = Bukkit.getPlayer(participant.getUniqueId());
        if (player == null) return;

        final IBentenganTeam playerTeam = participant.getTeam();
        if (playerTeam == null) return;

        final IBentenganTeam opponentTeam = teamHelper.getOppositeTeam(playerTeam);
        if (opponentTeam == null) return;

        player.teleport(opponentTeam.getJailSpawnPoint());

        final ChatColor participantTeamColor = ChatColor.valueOf(playerTeam.getColor());
        arenaAnnouncer.sendGlobalMessage(participantTeamColor + player.getName() + ChatColor.RED + " has been jailed!");

        player.sendMessage(ChatColor.RED + "You have been jailed!");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.8F);
        player.sendTitle(
                ChatColor.RED + "You have been jailed!",
                ChatColor.GRAY + "You teammates must free you!",
                0,
                100,
                10
        );
    }

    public boolean isJailed(final IBentenganPlayer participant) {
        final Player player = Bukkit.getPlayer(participant.getUniqueId());
        if (player == null) return false;

        final IBentenganTeam playerTeam = participant.getTeam();
        if (playerTeam == null) return false;

        final IBentenganTeam opponentTeam = teamHelper.getOppositeTeam(playerTeam);
        if (opponentTeam == null) return false;

        return opponentTeam.getJailZone().containsLocation(player.getLocation());
    }
}
