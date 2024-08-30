package com.covercorp.holosports.game.minigame.potato.arena;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.potato.arena.command.*;
import com.covercorp.holosports.game.minigame.potato.arena.listener.PotatoMatchListener;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.announcer.ArenaAnnouncer;
import com.covercorp.holosports.game.minigame.potato.arena.checkpoint.RaceCheckpoint;
import com.covercorp.holosports.game.minigame.potato.arena.inventory.PotatoGameItemCollection;
import com.covercorp.holosports.game.minigame.potato.arena.properties.PotatoMatchProperties;
import com.covercorp.holosports.game.minigame.potato.arena.task.*;
import com.covercorp.holosports.game.minigame.potato.config.PotatoConfigHelper;
import com.covercorp.holosports.game.minigame.potato.player.IPotatoPlayerHelper;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.IPotatoTeamHelper;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;

import com.covercorp.holosports.game.util.NicePlayersUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter(AccessLevel.PUBLIC)
public final class PotatoArena {
    private final PotatoMiniGame potatoMiniGame;
    private final IPotatoPlayerHelper playerHelper;
    private final IPotatoTeamHelper teamHelper;

    private final PotatoMatchProperties potatoMatchProperties;

    private final Location lobbyLocation;

    private final RaceCheckpoint startCheckpoint;
    private final RaceCheckpoint midCheckpoint;

    private final ArenaAnnouncer arenaAnnouncer;

    @Setter(AccessLevel.PUBLIC) private PotatoMatchState state;

    @Setter(AccessLevel.PUBLIC) private int gameTime = 0;

    private int arenaTickTask = -1;
    private int arenaTimeTask = -1;
    private int arenaActionBarTask = -1;

    private int fireworkTaskId = -1;

    @Setter(AccessLevel.PUBLIC) private IPotatoTeam winnerTeam;

    private final int raceLaps = 3;

    public PotatoArena(final PotatoMiniGame potatoMiniGame) {
        this.potatoMiniGame = potatoMiniGame;

        playerHelper = potatoMiniGame.getPlayerHelper();
        teamHelper = potatoMiniGame.getTeamHelper();

        potatoMatchProperties = new PotatoMatchProperties(this);

        arenaAnnouncer = new ArenaAnnouncer(getPlayerHelper());

        final PotatoConfigHelper config = potatoMiniGame.getPotatoConfigHelper();

        lobbyLocation = config.getLobbySpawn();

        startCheckpoint = new RaceCheckpoint(config.getGoalLocation("first"), config.getGoalCuboid("first"));
        midCheckpoint = new RaceCheckpoint(config.getGoalLocation("mid"), config.getGoalCuboid("mid"));

        state = PotatoMatchState.WAITING;

        potatoMiniGame.getHoloSportsGame().getServer().getPluginManager().registerEvents(new PotatoMatchListener(this), potatoMiniGame.getHoloSportsGame());

        // Armorstand cache
        final World world = lobbyLocation.getWorld();

        if (world != null) {
            /*world.getEntities().stream().filter(entity -> entity.getType() == EntityType.ARMOR_STAND).forEach(entity -> {
                //final boolean hasNbt = NBTMetadataUtil.hasEntityString(entity, "accessor");

                //if (!hasNbt) return;

                //if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("potato_sack")) {
                    HoloSportsGame.getHoloSportsGame().getLogger().info("Removing potato sack: " + entity.getUniqueId());
                    entity.remove();
                //}
            });*/
            world.getLivingEntities().stream().filter(entity -> entity.getType() == EntityType.ARMOR_STAND).forEach(entity -> {
                //final boolean hasNbt = NBTMetadataUtil.hasEntityString(entity, "accessor");

                //if (!hasNbt) return;

                //if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("potato_sack")) {
                HoloSportsGame.getHoloSportsGame().getLogger().info("Removing potato sack: " + entity.getUniqueId());
                entity.remove();
                //}
            });
        }

        // COMMANDS
        new AddPlayerToTeamCommand(this).register(potatoMiniGame.getHoloSportsGame());
        new RemovePlayerToTeamCommand(this).register(potatoMiniGame.getHoloSportsGame());
        new ClearTeamPlayersCommand(this).register(potatoMiniGame.getHoloSportsGame());

        // GLOBAL COMMANDS
        new StartMatchCommand(this).register(potatoMiniGame.getHoloSportsGame());
        new StopMatchCommand(this).register(potatoMiniGame.getHoloSportsGame());
    }

    public void start() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=armor_stand]");

        Bukkit.getScheduler().cancelTask(potatoMatchProperties.getStartingTaskId());

        arenaTickTask = Bukkit.getScheduler().runTaskTimer(potatoMiniGame.getHoloSportsGame(), new PotatoTickTask(this), 0L, 1L).getTaskId();

        Bukkit.getOnlinePlayers().forEach(player -> {
            final Optional<IPotatoPlayer> potatoPlayerOptional = playerHelper.getPlayer(player.getUniqueId());
            if (potatoPlayerOptional.isEmpty()) {
                player.sendMessage(CommonUtil.colorize("&a[!] You are now spectating the match! You can fly!"));
                player.setAllowFlight(true);
            }
        });

        playerHelper.getPlayerList().forEach(participant -> {
            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            player.setAllowFlight(false);

            final IPotatoTeam team = participant.getTeam();
            if (team == null) return;

            // Create an invisible armorstand for the player
            final ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setCustomNameVisible(false);

            NBTMetadataUtil.addStringToEntity(armorStand, "accessor", "potato_sack");

            armorStand.getEquipment().setHelmet(new ItemBuilder(Material.LEATHER_HORSE_ARMOR).withCustomModelData(1000).build());

            participant.setArmorStand(armorStand);

            player.teleport(startCheckpoint.getCheckpointLocation());
        });

        // Announce the game start, and the subtitle must be player1 vs player2, or player1 & player2 vs player3 & player4
        final Set<IPotatoPlayer> team1Players = teamHelper.getTeamList().get(0).getPlayers();
        final Set<IPotatoPlayer> team2Players = teamHelper.getTeamList().get(1).getPlayers();

        getArenaAnnouncer().sendGlobalTitle(
                "&6&lPOTATO SACK RACE",
                "&7" + team1Players.stream().map(IPotatoPlayer::getName).collect(Collectors.joining(" &f&&7 ")) + " &fvs&7 " + team2Players.stream().map(IPotatoPlayer::getName).collect(Collectors.joining(" &f&&7 ")),
                0,
                40,
                20);

        getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F);

        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        getArenaAnnouncer().sendGlobalCenteredMessage("&f&lPotato Sack Race");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lGet pass through the obstacles and reach the goal");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lpoint.");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lThe first team to have all their players reach the");
        getArenaAnnouncer().sendGlobalCenteredMessage("&e&lgoal point 3 times wins the game!");
        getArenaAnnouncer().sendGlobalMessage("&0 ");
        getArenaAnnouncer().sendGlobalMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        getPotatoMatchProperties().setRaceStarting(true);
        getPotatoMatchProperties().setRaceStartingTaskId(Bukkit.getScheduler().runTaskTimer(potatoMiniGame.getHoloSportsGame(), new RaceStartTask(this), 0L, 20L).getTaskId());

        setState(PotatoMatchState.RACE_STARTING);
    }

    public void startRace() {
        state = PotatoMatchState.GAME;

        Bukkit.getScheduler().cancelTask(potatoMatchProperties.getRaceStartingTaskId());

        arenaTimeTask = Bukkit.getScheduler().runTaskTimer(potatoMiniGame.getHoloSportsGame(), new PotatoTimeTask(this), 0L, 20L).getTaskId();
        arenaActionBarTask = Bukkit.getScheduler().runTaskTimer(potatoMiniGame.getHoloSportsGame(), new MatchActionBarTask(potatoMiniGame, this), 0L, 20L).getTaskId();

        playerHelper.getPlayerList().forEach(participant -> {
            final Player player = Bukkit.getPlayer(participant.getUniqueId());
            if (player == null) return;

            participant.resetBossBar();

            participant.setBossBar(Bukkit.createBossBar(
                    "Jump Power",
                    BarColor.GREEN,
                    BarStyle.SOLID
            ));

            //participant.getBossBar().addPlayer(player);
            participant.getBossBar().setProgress(0.0);
        });

        getArenaAnnouncer().sendGlobalMessage("&eYou can now start jumping! Good luck!");
        getArenaAnnouncer().sendGlobalTitle("&a&lJUMP!", "", 0, 20, 10);
        getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0F, 2.0F);

        setState(PotatoMatchState.GAME);
    }

    public void stop() {
        state = PotatoMatchState.ENDING;

        stopTasks();

        final IPotatoPlayerHelper playerHelper = potatoMiniGame.getPlayerHelper();
        final IPotatoTeamHelper teamHelper = potatoMiniGame.getTeamHelper();

        if (winnerTeam == null) {
            getArenaAnnouncer().sendGlobalMessage(" \n&6&lThe game ended with a TIE!, this should not be happening?");
            getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_CAT_PURREOW, 0.8F, 0.8F);

            playerHelper.getPlayerList().forEach(potatoPlayer -> {
                final Player player = Bukkit.getPlayer(potatoPlayer.getUniqueId());
                if (player == null) return;

                if (potatoPlayer.getArmorStand() != null) {
                    potatoPlayer.getArmorStand().remove();
                    potatoPlayer.setArmorStand(null);
                }
            });
        } else {
            final ChatColor winnerTeamColor = ChatColor.valueOf(winnerTeam.getColor());
            getArenaAnnouncer().sendGlobalTitle(
                    "&aGame ended!",
                    winnerTeamColor + winnerTeam.getPlayers().stream().map(IPotatoPlayer::getName).collect(Collectors.joining(" &f& " + winnerTeamColor)) + " &7won the match!",
                    0,
                    100,
                    20
            );

            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            getArenaAnnouncer().sendGlobalCenteredMessage("&e&lMatch ended!");
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&eWinner team: " + ChatColor.valueOf(winnerTeam.getColor()) + winnerTeam.getName());
            getArenaAnnouncer().sendGlobalCenteredMessage("&7" + winnerTeam.getPlayers().stream().map(IPotatoPlayer::getName).collect(Collectors.joining(" &f&&7 ")));
            getArenaAnnouncer().sendGlobalCenteredMessage(" ");
            getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 0.8F));

            playerHelper.getPlayerList().forEach(potatoPlayer -> {
                final Player player = Bukkit.getPlayer(potatoPlayer.getUniqueId());
                if (player == null) return;

                if (potatoPlayer.getArmorStand() != null) {
                    potatoPlayer.getArmorStand().remove();
                    potatoPlayer.setArmorStand(null);
                }
            });

            fireworkTaskId = Bukkit.getScheduler().runTaskTimer(potatoMiniGame.getHoloSportsGame(), new MatchWinnerFireworksTask(this, winnerTeam), 0L, 20L).getTaskId();

            Bukkit.getScheduler().runTaskLater(potatoMiniGame.getHoloSportsGame(), () -> {
                Bukkit.getScheduler().cancelTask(fireworkTaskId);
                fireworkTaskId = -1;
            }, 20L * 3L);
        }

        // Clear team data
        Bukkit.getScheduler().runTaskLater(potatoMiniGame.getHoloSportsGame(), () -> {
            playerHelper.getPlayerList().forEach(potatoPlayer -> {
                final Player player = Bukkit.getPlayer(potatoPlayer.getUniqueId());
                if (player == null) return;

                if (potatoPlayer.getArmorStand() != null) {
                    potatoPlayer.getArmorStand().remove();
                    potatoPlayer.setArmorStand(null);
                }

                PotatoGameItemCollection.resetPlayerHotbar(potatoPlayer);

                final IPotatoTeam team = potatoPlayer.getTeam();

                if (team != null) teamHelper.removePlayerFromTeam(potatoPlayer, team.getIdentifier());

                potatoPlayer.resetBossBar();

                playerHelper.removePlayer(potatoPlayer.getUniqueId());
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

                    inventory.setItem(5, PotatoGameItemCollection.getStartItem());
                    inventory.setItem(6, PotatoGameItemCollection.getStopItem());
                }
            });

            // Reset starting properties
            getPotatoMatchProperties().resetTimer();

            setState(PotatoMatchState.WAITING);
        }, 120L);
    }

    public void finishPlayer(final IPotatoPlayer participant) {
        final Player player = Bukkit.getPlayer(participant.getUniqueId());
        if (player == null) return;

        player.setGameMode(GameMode.SPECTATOR);

        arenaAnnouncer.sendGlobalMessage("&e" + participant.getName() + " &7finished the race!");
        participant.setFinishedRace(true);

        final IPotatoTeam team = participant.getTeam();
        if (team == null) return;

        if (team.reachedGoal()) {
            setWinnerTeam(team);
            stop();
        }
    }

    private void stopTasks() {
        Bukkit.getScheduler().cancelTask(arenaTickTask);
        arenaTickTask = -1;
        Bukkit.getScheduler().cancelTask(arenaTimeTask);
        arenaTimeTask = -1;
        Bukkit.getScheduler().cancelTask(arenaActionBarTask);
        arenaActionBarTask = -1;
        Bukkit.getScheduler().cancelTask(potatoMatchProperties.getRaceStartingTaskId());
    }
}
