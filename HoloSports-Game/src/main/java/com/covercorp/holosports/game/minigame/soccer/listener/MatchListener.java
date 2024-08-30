package com.covercorp.holosports.game.minigame.soccer.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;

import com.covercorp.holosports.game.minigame.soccer.arena.task.StartingTask;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
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
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena arena;

    public MatchListener(SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        arena = soccerMiniGame.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();

        if (NBTMetadataUtil.hasString(event.getItemDrop().getItemStack(), "accessor")) {

            event.setCancelled(true);
        }

        final Optional<ISoccerPlayer> soccerPlayerOptional = soccerMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (soccerPlayerOptional.isEmpty()) return;

        if (arena.getState() != SoccerMatchState.WAITING) {
            player.sendMessage(ChatColor.RED + "You can't drop items while playing!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;

        if (NBTMetadataUtil.hasString(event.getCurrentItem(), "accessor")) {
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

        final String accessor = NBTMetadataUtil.getString(itemStack, "accessor");

        if (accessor.equalsIgnoreCase("resume_game")) {
            if (arena.getState() != SoccerMatchState.PAUSED) {
                sender.sendMessage(ChatColor.RED + "The game is not paused!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Soccer] Resuming game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to RESUME the game.");
            arena.resume();
        }
        if (accessor.equalsIgnoreCase("start_game")) {
            if (arena.getState() != SoccerMatchState.WAITING) {
                sender.sendMessage(ChatColor.RED + "The game is already started!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Soccer] Starting game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to START the game.");

            if (soccerMiniGame.getPlayerHelper().getPlayerList().stream().filter(p -> !p.isReferee()).toList().size() < 2) {
                sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
                sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
                return;
            }

            if (soccerMiniGame.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
                sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
                sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

                sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
                soccerMiniGame.getTeamHelper().getTeamList().forEach(team -> {
                    if (team.getPlayers().size() == 0) {
                        sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                    }
                });
                return;
            }

            if (soccerMiniGame.getPlayerHelper().noRolePlayers().size() != 0) {
                sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
                sender.sendMessage(CommonUtil.colorize("&cThere are players without a role!\n "));

                sender.sendMessage(CommonUtil.colorize("&7The following participants don't have a role:"));
                soccerMiniGame.getPlayerHelper().noRolePlayers().forEach(soccerPlayer -> {
                    sender.sendMessage(CommonUtil.colorize("&8- &f" + soccerPlayer.getName() + " &7[Team " + ChatColor.valueOf(soccerPlayer.getTeam().getColor()) + soccerPlayer.getTeam().getName() + "&7]"));
                });
                return;
            }

            if (!arena.getSoccerMatchProperties().isStarting()) {
                arena.getSoccerMatchProperties().setStarting(true);
                arena.getSoccerMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new StartingTask(arena), 0L, 20L).getTaskId());

                sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");
                sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

                return;
            }

            Bukkit.getScheduler().cancelTask(arena.getSoccerMatchProperties().getStartingTaskId());

            arena.getSoccerMatchProperties().resetTimer();

            arena.setState(SoccerMatchState.WAITING);

            sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
            sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
        }
        if (accessor.equalsIgnoreCase("stop_game")) {
            if (arena.getState() != SoccerMatchState.GAME) {
                sender.sendMessage(ChatColor.RED + "The game is not started! If the game is paused, you must resume it first!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Soccer] Stopping game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to STOP the game.");
            arena.stop();
        }
    }
}
