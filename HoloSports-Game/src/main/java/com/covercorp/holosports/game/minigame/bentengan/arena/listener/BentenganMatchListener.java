package com.covercorp.holosports.game.minigame.bentengan.arena.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.arena.listener.event.BentenganMatchTickEvent;
import com.covercorp.holosports.game.minigame.bentengan.arena.task.StartingTask;
import com.covercorp.holosports.game.minigame.bentengan.player.IBentenganPlayerHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.IBentenganTeamHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BentenganMatchListener implements Listener {
    private final BentenganArena arena;
    private final List<Cuboid> matchZones = new ArrayList<>();

    public BentenganMatchListener(final BentenganArena arena) {
        this.arena = arena;

        final IBentenganTeamHelper teamHelper = arena.getTeamHelper();

        teamHelper.getTeamList().forEach(team -> {
            matchZones.add(team.getZone());
        });

        matchZones.add(arena.getMidZone());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();

        if (event.getItemDrop().getItemStack().getType() == Material.BLAZE_POWDER) {
            event.setCancelled(true);
        }
        if (event.getItemDrop().getItemStack().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }

        final Optional<IBentenganPlayer> bentenganPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (bentenganPlayerOptional.isEmpty()) return;

        if (arena.getState() != BentenganMatchState.WAITING) {
            player.sendMessage(ChatColor.RED + "You can't drop items while playing!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getType() == Material.BLAZE_POWDER) {
            event.setCancelled(true);
        }
        if (event.getCurrentItem().getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

    /*
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(final PlayerMoveEvent event) {
        final IBentenganPlayerHelper playerHelper = arena.getPlayerHelper();
        final Player player = event.getPlayer();

        if (playerHelper.getPlayer(player.getUniqueId()).isEmpty()) {
            // Check if any matchZones contains the player location
            if (matchZones.stream().anyMatch(cuboid -> cuboid.containsLocation(player.getLocation()))) {
                player.teleport(arena.getLobbyLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                player.sendMessage(CommonUtil.colorize("&cYou are not allowed to enter the match area!"));
            }
        }
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTick(final BentenganMatchTickEvent event) {
        final IBentenganPlayerHelper playerHelper = arena.getPlayerHelper();
        final IBentenganTeamHelper teamHelper = arena.getTeamHelper();

        if (arena.getState() == BentenganMatchState.GAME) {
            playerHelper.getPlayerList().forEach(participant -> {
                final IBentenganTeam participantTeam = participant.getTeam();
                if (participantTeam == null) return;

                final IBentenganTeam oppositeTeam = arena.getTeamHelper().getOppositeTeam(participantTeam);
                if (oppositeTeam == null) return;

                final Player player = Bukkit.getPlayer(participant.getUniqueId());
                if (player == null) return;

                // Check if we need to jail someone.
                if (oppositeTeam.getJailZone().containsLocation(player.getLocation()) && !arena.isJailed(participant)) {
                    arena.jailPlayer(participant);
                }

                // Check if some team won by capturing the beacon.
                if (oppositeTeam.getBeaconZone().containsLocation(player.getLocation())) {
                    arena.setWinnerTeam(participantTeam);
                    arena.stop();
                    arena.getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    arena.getArenaAnnouncer().sendGlobalMessage("&6¡&e" + player.getName() + " &6touched the beacon zone! &lTeam " + ChatColor.valueOf(participantTeam.getColor()) + participantTeam.getName() + " &6&lwins!");
                    arena.getArenaAnnouncer().sendGlobalCenteredMessage("&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                }
            });
        }

        // I must check again if the state is still GAME, because the arena might have been stopped by the code above.
        if (arena.getState() == BentenganMatchState.GAME) {
            teamHelper.getTeamList().forEach(team -> {
                // Check if every team member is jailed, if so, the opposite team wins.
                final List<IBentenganPlayer> jailedTeamPlayers = new ArrayList<>();

                team.getPlayers().forEach(player -> {
                    if (arena.isJailed(player)) {
                        jailedTeamPlayers.add(player);
                    }
                });

                if (jailedTeamPlayers.size() == team.getPlayers().size()) {
                    arena.setWinnerTeam(teamHelper.getOppositeTeam(team));
                    arena.stop();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (!((event.getEntity()) instanceof final Player player)) return;

        if (arena.getState() != BentenganMatchState.GAME) {
            event.setCancelled(true);
            return;
        }

        final Optional<IBentenganPlayer> bentenganPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (bentenganPlayerOptional.isEmpty()) return;

        final IBentenganPlayer bentenganPlayer = bentenganPlayerOptional.get();
        if (arena.isJailed(bentenganPlayer)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamageByPlayer(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final Player damaged)) return;
        if (!(event.getDamager() instanceof final Player damager)) return;

        final Optional<IBentenganPlayer> damagedBentenganPlayerOptional = arena.getPlayerHelper().getPlayer(damaged.getUniqueId());
        if (damagedBentenganPlayerOptional.isEmpty()) return;

        final IBentenganPlayer damagedBentenganPlayer = damagedBentenganPlayerOptional.get();
        if (arena.isJailed(damagedBentenganPlayer)) {
            event.setCancelled(true);
            return;
        }

        final Optional<IBentenganPlayer> damagerBentenganPlayerOptional = arena.getPlayerHelper().getPlayer(damager.getUniqueId());
        if (damagerBentenganPlayerOptional.isEmpty()) return;

        final IBentenganPlayer damagerBentenganPlayer = damagerBentenganPlayerOptional.get();
        if (arena.isJailed(damagerBentenganPlayer)) {
            event.setCancelled(true);
            return;
        }

        final IBentenganTeam damagedTeam = damagedBentenganPlayer.getTeam();
        if (damagedTeam == null) return;

        final IBentenganTeam damagerTeam = damagerBentenganPlayer.getTeam();
        if (damagerTeam == null) return;

        if (arena.getMidZone().containsLocation(damaged.getLocation())) {
            event.setCancelled(false);
            return;
        }

        // Check if the damaged participant is in the same team as the damager.
        if (damagedTeam.getIdentifier().equals(damagerTeam.getIdentifier())) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        // Check if the damaged player is inside the damager team's zone
        if (damagerTeam.getZone().containsLocation(damaged.getLocation())) {
            // Jail the damaged player.
            arena.jailPlayer(damagedBentenganPlayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (arena.getState() != BentenganMatchState.GAME) return;

        final Player player = event.getEntity();

        // Check if any drop is a barrier or a blaze powder, if so, remove it.
        event.getDrops().removeIf(itemStack -> itemStack.getType() == Material.BARRIER || itemStack.getType() == Material.BLAZE_POWDER || itemStack.getType() == Material.LEATHER_HELMET);

        Bukkit.getScheduler().runTaskLater(arena.getBentenganMiniGame().getHoloSportsGame(), () -> {
            player.spigot().respawn();

            final Optional<IBentenganPlayer> bentenganPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
            if (bentenganPlayerOptional.isEmpty()) return;

            final IBentenganPlayer bentenganPlayer = bentenganPlayerOptional.get();

            BentenganGameItemCollection.resetPlayerHotbar(bentenganPlayer);
            BentenganGameItemCollection.setupPlayerHotbar(bentenganPlayer);

            arena.jailPlayer(bentenganPlayer);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(final PlayerInteractEvent event) {
        final Player sender = event.getPlayer();

        final ItemStack itemStack = sender.getInventory().getItemInMainHand();

        if (itemStack.getType() == Material.AIR) return;

        if (!NBTMetadataUtil.hasString(itemStack, "accessor")) return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        final String accessor = NBTMetadataUtil.getString(itemStack, "accessor");

        if (itemStack.getType() == Material.BLAZE_POWDER) {
            event.setCancelled(true);

            if (arena.getState() != BentenganMatchState.WAITING) {
                sender.sendMessage(ChatColor.RED + "The game is already started!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Bentengan] Starting game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to START the game.");

            if (arena.getPlayerHelper().getPlayerList().size() < 2) {
                sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
                sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
                return;
            }

            if (arena.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
                sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
                sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

                sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
                arena.getTeamHelper().getTeamList().forEach(team -> {
                    if (team.getPlayers().size() == 0) {
                        sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                    }
                });
                return;
            }

            if (!arena.getBentenganMatchProperties().isStarting()) {
                arena.getBentenganMatchProperties().setStarting(true);
                arena.getBentenganMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(arena.getBentenganMiniGame().getHoloSportsGame(), new StartingTask(arena), 0L, 20L).getTaskId());

                sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");
                sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

                return;
            }

            Bukkit.getScheduler().cancelTask(arena.getBentenganMatchProperties().getStartingTaskId());

            arena.getBentenganMatchProperties().resetTimer();

            arena.setState(BentenganMatchState.WAITING);

            sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
            sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
        }
        if (itemStack.getType() == Material.BARRIER) {
            event.setCancelled(true);

            if (arena.getState() != BentenganMatchState.GAME) {
                sender.sendMessage(ChatColor.RED + "The game is not started! If the game is paused, you must resume it first!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Bentengan] Stopping game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to STOP the game.");
            arena.stop();
        }
    }
}
