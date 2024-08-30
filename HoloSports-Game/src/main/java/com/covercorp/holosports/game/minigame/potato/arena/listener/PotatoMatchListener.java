package com.covercorp.holosports.game.minigame.potato.arena.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.listener.event.PotatoMatchTickEvent;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.arena.task.StartingTask;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public final class PotatoMatchListener implements Listener {
    private final PotatoArena arena;

    public PotatoMatchListener(final PotatoArena arena) {
        this.arena = arena;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTick(final PotatoMatchTickEvent event) {
        final Cuboid startingCuboid = arena.getStartCheckpoint().getCheckpointCuboid();
        final Location startingCuboidCenter = arena.getStartCheckpoint().getCheckpointLocation();
        final Cuboid midCuboid = arena.getMidCheckpoint().getCheckpointCuboid();

        final PotatoMatchState state = event.getArena().getState();

        if (state != PotatoMatchState.WAITING) {
            arena.getPlayerHelper().getPlayerList().forEach(participant -> {
                final Player player = Bukkit.getPlayer(participant.getUniqueId());
                if (player == null) return;

                final ArmorStand armorStand = participant.getArmorStand();
                if (armorStand == null) return;

                armorStand.teleport(player.getLocation().add(0, -0.25, 0));
            });
        }

        if (state == PotatoMatchState.STARTING || state == PotatoMatchState.RACE_STARTING) {
            // Check if any participant is outside the starting cuboid
            // If any, teleport them to the starting cuboid center location
            arena.getPlayerHelper().getPlayerList().forEach(participant -> {
                final Player player = Bukkit.getPlayer(participant.getUniqueId());
                if (player == null) return;

                if (!startingCuboid.containsLocation(player.getLocation())) {
                    player.sendMessage(ChatColor.RED + "You cannot leave the starting area yet!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    player.teleport(startingCuboidCenter);
                }
            });

            return;
        }

        if (state == PotatoMatchState.GAME) {
            arena.getPlayerHelper().getPlayerList().forEach(participant -> {
                final Player player = Bukkit.getPlayer(participant.getUniqueId());
                if (player == null) return;

                // Apply the slowness effect
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 254, false, false, false));

                /*
                // Jump power system
                if (player.isSneaking()) {
                    if (participant.getJumpPower() < 100) {
                        participant.setJumpPower(participant.getJumpPower() + 5);
                    }
                } else {
                    if (participant.getJumpPower() > 0) {
                        participant.setJumpPower(0);
                    }
                }

                // Fill the bossbar, the max jump power is 100, and the max float value is 1.0F
                participant.getBossBar().setProgress(participant.getJumpPower() / 100.0f);

                if (participant.getJumpPower() >= 80) {
                    //player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                    player.removePotionEffect(PotionEffectType.JUMP);
                    //player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 255, false, false, false));
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 128, false, false, false));
                }*/

                // Winning system
                if (midCuboid.containsLocation(player.getLocation()) && !participant.isTouchedHalf()) {
                    participant.setTouchedHalf(true);
                }

                if (startingCuboid.containsLocation(player.getLocation()) && !participant.isTouchedGoal() && participant.isTouchedHalf()) {
                    participant.setTouchedGoal(true);
                }

                if (!participant.isFinishedRace()) {
                    if (participant.isTouchedHalf() && participant.isTouchedGoal()) {
                        participant.setFinishedLaps(participant.getFinishedLaps() + 1);

                        player.playEffect(EntityEffect.FIREWORK_EXPLODE);

                        if (participant.getFinishedLaps() >= 3) {
                            player.sendMessage(ChatColor.GREEN + "You finished your last lap!");
                            arena.finishPlayer(participant);
                            return;
                        }

                        player.sendMessage(ChatColor.GREEN + "You finished a lap!");
                        player.sendMessage(ChatColor.GREEN + "Laps left: " + (arena.getRaceLaps() - participant.getFinishedLaps()));

                        arena.getArenaAnnouncer().sendGlobalMessage("&e" + player.getName() + " &efinished a lap! &7(" + participant.getFinishedLaps() + "/" + arena.getRaceLaps() + ")");
                        arena.getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);

                        participant.setTouchedHalf(false);
                        participant.setTouchedGoal(false);
                    }
                }
            });
        }
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

        final Optional<IPotatoPlayer> potatoPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (potatoPlayerOptional.isEmpty()) return;

        if (arena.getState() != PotatoMatchState.WAITING) {
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

        //if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        //final String accessor = NBTMetadataUtil.getString(itemStack, "accessor");

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack.getType() == Material.BLAZE_POWDER) {
                if (arena.getState() != PotatoMatchState.WAITING) {
                    sender.sendMessage(ChatColor.RED + "The game is already started!");
                    return;
                }

                sender.sendMessage(ChatColor.GREEN + "[Potato] Starting game...");
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

                if (!arena.getPotatoMatchProperties().isStarting()) {
                    arena.getPotatoMatchProperties().setStarting(true);
                    arena.getPotatoMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(arena.getPotatoMiniGame().getHoloSportsGame(), new StartingTask(arena), 0L, 20L).getTaskId());

                    sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");
                    sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                    Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

                    return;
                }

                Bukkit.getScheduler().cancelTask(arena.getPotatoMatchProperties().getStartingTaskId());

                arena.getPotatoMatchProperties().resetTimer();

                arena.setState(PotatoMatchState.WAITING);

                sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
                sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
            }
            if (itemStack.getType() == Material.BARRIER) {
                if (arena.getState() != PotatoMatchState.GAME) {
                    sender.sendMessage(ChatColor.RED + "The game is not started! If the game is paused, you must resume it first!");
                    return;
                }

                sender.sendMessage(ChatColor.GREEN + "[Potato] Stopping game...");
                arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to STOP the game.");
                arena.stop();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorStandInteract(final PlayerInteractAtEntityEvent event) {
        final Player sender = event.getPlayer();
        if (!(event.getRightClicked() instanceof final ArmorStand armorStand)) return;

        final EntityEquipment equipment = armorStand.getEquipment();
        if (equipment == null) return;

        final ItemStack helmet = equipment.getHelmet();
        if (helmet == null) return;

        if (helmet.getType() == Material.LEATHER_HORSE_ARMOR) {
            event.setCancelled(true);
        }

        final ItemStack itemStack = sender.getInventory().getItemInMainHand();

        if (itemStack.getType() == Material.BLAZE_POWDER) {
            event.setCancelled(true);

            if (arena.getState() != PotatoMatchState.WAITING) {
                sender.sendMessage(ChatColor.RED + "The game is already started!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Potato] Starting game...");
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

            if (!arena.getPotatoMatchProperties().isStarting()) {
                arena.getPotatoMatchProperties().setStarting(true);
                arena.getPotatoMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(arena.getPotatoMiniGame().getHoloSportsGame(), new StartingTask(arena), 0L, 20L).getTaskId());

                sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");
                sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

                return;
            }

            Bukkit.getScheduler().cancelTask(arena.getPotatoMatchProperties().getStartingTaskId());

            arena.getPotatoMatchProperties().resetTimer();

            arena.setState(PotatoMatchState.WAITING);

            sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
            sender.playSound(sender, Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
        }
        if (itemStack.getType() == Material.BARRIER) {
            event.setCancelled(true);

            if (arena.getState() != PotatoMatchState.GAME) {
                sender.sendMessage(ChatColor.RED + "The game is not started! If the game is paused, you must resume it first!");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "[Potato] Stopping game...");
            arena.getArenaAnnouncer().sendGlobalMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " is trying to STOP the game.");
            arena.stop();
        }
    }
}
