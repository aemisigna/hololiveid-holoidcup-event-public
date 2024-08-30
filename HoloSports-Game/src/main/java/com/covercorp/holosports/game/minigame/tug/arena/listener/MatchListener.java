package com.covercorp.holosports.game.minigame.tug.arena.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.arena.task.StartingTask;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class MatchListener implements Listener {
    private final TugMiniGame tugMiniGame;
    private final TugArena arena;

    public MatchListener(final TugMiniGame tugMiniGame, final TugArena arena) {
        this.tugMiniGame = tugMiniGame;
        this.arena = arena;
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

        final Optional<ITugPlayer> tugPlayerOptional = tugMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (tugPlayerOptional.isEmpty()) return;

        if (arena.getState() != TugMatchState.WAITING) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(final PlayerInteractEvent event) {
        final Player sender = event.getPlayer();

        final ItemStack itemStack = sender.getInventory().getItemInMainHand();

        if (itemStack.getType() == Material.AIR) return;

        if (!NBTMetadataUtil.hasString(itemStack, "accessor")) return;

        event.setCancelled(true);

        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        //final String accessor = NBTMetadataUtil.getString(itemStack, "accessor");

        if (itemStack.getType() == Material.BLAZE_POWDER) {
            if (arena.getState() != TugMatchState.WAITING) {
                sender.sendMessage(ChatColor.RED + "The game is already started!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Tug] Starting game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to START the game.");

            if (tugMiniGame.getPlayerHelper().getPlayerList().size() < 2) {
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

            if (!arena.getTugMatchProperties().isStarting()) {
                arena.getTugMatchProperties().setStarting(true);
                arena.getTugMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(tugMiniGame.getHoloSportsGame(), new StartingTask(arena), 0L, 20L).getTaskId());

                sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");
                sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

                return;
            }

            Bukkit.getScheduler().cancelTask(arena.getTugMatchProperties().getStartingTaskId());

            arena.getTugMatchProperties().resetTimer();

            arena.setState(TugMatchState.WAITING);

            sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
            sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
        }
        if (itemStack.getType() == Material.BARRIER) {
            if (arena.getState() != TugMatchState.GAME) {
                sender.sendMessage(ChatColor.RED + "The game is not started! If the game is paused, you must resume it first!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Tug] Stopping game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to STOP the game.");
            arena.stop();
        }
    }
}
