package com.covercorp.holosports.game.minigame.soccer.listener;

import com.covercorp.holosports.commons.util.BlockUtil;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;

import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.task.BallSpawnTask;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.inventory.item.GameItemType;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;

import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import com.covercorp.holosports.game.minigame.soccer.util.EffectUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class BallListener implements Listener {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena arena;

    private final List<UUID> flaggedShooters;

    public BallListener(SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        arena = soccerMiniGame.getArena();

        flaggedShooters = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (NBTMetadataUtil.hasEntityString(event.getEntity(), "accessor")) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorStandClick(final EntityDamageByEntityEvent event) {
        final Entity eventEntity = event.getEntity();

        if (event.getDamager().getType() != EntityType.PLAYER) return;

        if (arena.getState() != SoccerMatchState.GAME) return;

        final Player player = (Player) event.getDamager();
        final Optional<ISoccerPlayer> soccerPlayerOptional = soccerMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());

        if (soccerPlayerOptional.isEmpty()) {
            player.sendMessage("You are not a participant!");
            return;
        }

        final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
        final ISoccerTeam soccerTeam = soccerPlayer.getTeam();
        if (soccerTeam == null) return;

        if (soccerPlayer.getRole() == SoccerRole.VIEWER) return;

        if (eventEntity.getType() == EntityType.ARMOR_STAND || eventEntity.getType() == EntityType.SLIME) {
            if (!NBTMetadataUtil.hasEntityString(eventEntity, "accessor")) return;

            final SoccerBall soccerBall = arena.getSoccerBall();

            if (soccerBall == null) return;

            final UUID baseId = soccerBall.getBallArmorStand().getBaseStand().getUniqueId();
            final UUID hitboxId = soccerBall.getBallArmorStand().getHitboxStand().getUniqueId();

            ArmorStand armorStand;

            if (eventEntity.getUniqueId() == baseId) {
                armorStand = soccerBall.getBallArmorStand().getBaseStand();
            } else if (eventEntity.getUniqueId() == hitboxId) {
                armorStand = soccerBall.getBallArmorStand().getBaseStand();
            } else {
                armorStand = null;
            }

            if (armorStand == null) return;

            event.setCancelled(true);

            // Check player item, if empty, return
            final ItemStack hitItem = player.getInventory().getItemInMainHand();
            if (hitItem.getType() == Material.AIR) return;

            if (!NBTMetadataUtil.hasString(hitItem, "game_item_type")) return;

            if (flaggedShooters.contains(player.getUniqueId())) return;

            if (soccerBall.getMandatoryTagger() != null) {
                if (!soccerPlayer.isReferee()) {
                    if (soccerBall.getMandatoryTagger().getUniqueId() != soccerPlayer.getUniqueId()) {
                        player.sendMessage(CommonUtil.colorize("&c&l[!] You cannot tag the ball right now! \n&c&lThe tagger must be &e&l" + soccerBall.getMandatoryTagger().getName() + "&c&l!"));
                        return;
                    }
                }
            }

            double force = 0.5; // Fuerza inicial
            final Vector direction = event.getDamager().getLocation().getDirection();

            // Get the nbt string of the used item
            final String hitItemNbt = NBTMetadataUtil.getString(hitItem, "game_item_type");
            final GameItemType hitItemType = GameItemType.valueOf(hitItemNbt);

            // Depending on the hitItemType, we need to change the force and momentum
            switch (hitItemType) {
                case KICK -> {
                    if (arena.isPenaltyMode()) {
                        force += 1.7;
                        direction.setY(direction.getY() + 0.15);
                    } else {
                        force += 2.15;
                        direction.setY(0.1);
                    }

                    if (soccerBall.isHolded()) soccerBall.setHolded(false);
                }
                case LONG_KICK -> {
                    if (arena.isPenaltyMode()) {
                        player.sendMessage(CommonUtil.colorize("&c&lYou cannot &e&llong &c&lkick the ball in penalty mode! Please use the &a&lgreen shoe&c&l to kick!"));
                        return;
                    } else {
                        force += 2.6;
                        direction.setY(0.5);
                    }

                    if (soccerBall.isHolded()) soccerBall.setHolded(false);
                }
                case HOLD -> {
                    if (soccerBall.isHolded()) {
                        player.sendMessage(ChatColor.RED + "You cannot hold the ball right now!");
                        return;
                    }

                    if (!arena.isPenaltyMode()) {
                        if (!soccerTeam.getGoalSafeCuboid().containsLocation(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "You cannot hold the ball outside your goal zone!");
                            return;
                        }
                    }

                    // Stop the ball
                    armorStand.setVelocity(new Vector(0, 0, 0));

                    final List<Block> nearbyPowderBlocks = BlockUtil.getNearbyBlocks(soccerTeam.getGoalKeeperSpawn(), Material.WHITE_CONCRETE_POWDER, 50);
                    // Get the closest block, if it not exists, then teleport the ball to the spawn location
                    if (nearbyPowderBlocks.size() == 0) {
                        soccerBall.despawn();
                        soccerBall.spawn(arena.getBallSpawnLocation());
                        return;
                    }

                    nearbyPowderBlocks.sort((a, b) -> {
                        final double aDistance = a.getLocation().distance(soccerTeam.getGoalKeeperSpawn());
                        final double bDistance = b.getLocation().distance(soccerTeam.getGoalKeeperSpawn());

                        return Double.compare(aDistance, bDistance);
                    });

                    // Spawn an instant firework at the armorstand's location
                    final Firework firework = (Firework) armorStand.getWorld().spawnEntity(armorStand.getLocation(), EntityType.FIREWORK);
                    final FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    final FireworkEffect effect = FireworkEffect.builder().withColor(Color.FUCHSIA).withFade(Color.SILVER).with(FireworkEffect.Type.BALL_LARGE).build();

                    fireworkMeta.addEffect(effect);
                    fireworkMeta.setPower(3);
                    firework.setFireworkMeta(fireworkMeta);

                    soccerBall.despawn();

                    if (!arena.isPenaltyMode()) {
                        soccerBall.setGraced(true);
                        soccerBall.setHolded(true);

                        // Announce that the ball has been holded by the player and will be respawned in 5 seconds using titles and make a firework sound
                        arena.getArenaAnnouncer().sendGlobalTitle("&6&lBALL HOLDED!", ChatColor.valueOf(soccerTeam.getColor()) + player.getName() + " &7has holded the ball!", 0, 20 * 3, 10);
                        arena.getArenaAnnouncer().sendGlobalMessage("&7[&r\uE230&7] &eThe ball will appear in &b5 seconds&e!");
                        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 0.8F);

                        Bukkit.getScheduler().runTaskLater(soccerMiniGame.getHoloSportsGame(), () -> {
                            final Block closestBlock = nearbyPowderBlocks.get(0);
                            final Location blockLocation = closestBlock.getLocation();

                            blockLocation.add(blockLocation.getX() > 0 ? 0.5 : -0.5, 0.0, blockLocation.getZ() > 0 ? 0.5 : -0.5);
                            blockLocation.setY(66);

                            soccerBall.setMandatoryTagger(soccerPlayer);

                            Bukkit.getScheduler().runTask(soccerMiniGame.getHoloSportsGame(), new BallSpawnTask(arena, soccerBall, blockLocation));

                            // Teleport the player who holded the ball near the ball
                            player.teleport(soccerTeam.getGoalKeeperSpawn());
                            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0F);
                        }, 20 * 5);

                    } else {
                        arena.getArenaAnnouncer().sendGlobalTitle("&6&lPENALTY HOLDED!", ChatColor.valueOf(soccerTeam.getColor()) + player.getName() + " &7has holded the ball!", 0, 20 * 3, 10);
                        arena.getArenaAnnouncer().sendGlobalMessage("&e[!] &b" + arena.getPenaltyKicker().getName() + " &emissed the penalty!");
                        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 0.8F);

                        arena.rotatePenaltyKicker();
                    }
                    return;
                }
            }

            //if (direction.getY() > 0.11) direction.setY(0.10);

            // Remove grace period after 1 second, this prevents the ball from being tagged twice outside
            if (soccerBall.isGraced()) {
                Bukkit.getScheduler().runTaskLater(soccerMiniGame.getHoloSportsGame(), () -> {
                    soccerBall.setGraced(false);
                }, 20);
            }

            if (soccerBall.getMandatoryTagger() != null) soccerBall.setMandatoryTagger(null);

            flaggedShooters.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(soccerMiniGame.getHoloSportsGame(), () -> flaggedShooters.remove(player.getUniqueId()), 20);

            armorStand.setVelocity(armorStand.getVelocity().subtract(new Vector(0.2, 0.0, 0.2)));

            final Vector normalizedDirection = direction.normalize();
            final Vector forceVector = normalizedDirection.clone().multiply(force);
            //final Vector vertical = normalizedDirection.add(new Vector(0.0, 0.7, 0.0)); // apply only if corner kick
            //final Vector currentVelocity = armorStand.getVelocity();
            armorStand.setVelocity(new Vector(0.0, 0.0, 0.0));
            final Vector currentVelocity = armorStand.getVelocity();
            final Vector newVelocity = currentVelocity.add(forceVector)/*.add(vertical)*/;

            soccerBall.setBallTagger(soccerPlayer);

            armorStand.setVelocity(newVelocity);
            armorStand.setGravity(false);

            EffectUtil.playShootEffect(armorStand.getLocation().add(0.0, 0.5, 0.0));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (arena.getState() != SoccerMatchState.GAME) return;

        final Optional<ISoccerPlayer> soccerPlayerOptional = soccerMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());

        if (soccerPlayerOptional.isEmpty()) return;

        final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();

        if (soccerPlayer.getRole() == SoccerRole.VIEWER) return;

        if (arena.isPenaltyMode()) return;

        // Playing
        final List<Entity> nearby = player.getNearbyEntities(0.5, 0.1, -0.5)
                .stream()
                .filter(entity ->
                        entity.getType() == EntityType.ARMOR_STAND ||
                        entity.getType() == EntityType.SLIME &&
                        NBTMetadataUtil.hasEntityString(entity, "accessor")
                )
                .toList();

        if (nearby.isEmpty()) return;
        if (event.getTo() == null) return;

        // Get the soccer ball and check nulls
        final SoccerBall soccerBall = arena.getSoccerBall();
        if (soccerBall == null) return;

        final Entity eventEntity = nearby.get(0);

        // Get the ball core
        ArmorStand armorStand;
        if (eventEntity.getUniqueId() == soccerBall.getBallArmorStand().getBaseStand().getUniqueId()) {
            armorStand = soccerBall.getBallArmorStand().getBaseStand();
        } else if (eventEntity.getUniqueId() == soccerBall.getBallArmorStand().getHitboxStand().getUniqueId()) {
            armorStand = soccerBall.getBallArmorStand().getBaseStand();
        } else {
            armorStand = null;
        }

        // If the entities don't match, return
        if (armorStand == null) return;

        // Check if referee
        if (soccerPlayer.isReferee()) return;

        if (soccerBall.isGraced()) return;

        final Vector direction = event.getTo().getDirection();

        direction.setY(0);

        double force = 0.3;

        final Vector a = direction.normalize();
        final Vector forceVector = a.clone().multiply(force);
        final Vector vertical = a.add(new Vector(0.0, 0.2, 0.0)); // apply only if corner kick
        final Vector currentVelocity = armorStand.getVelocity();
        final Vector newVelocity = currentVelocity.add(forceVector).add(vertical);

        // Rizky said that the ball doesn't need to be tagged if the player touches the ball without an item cast.
        soccerBall.setBallTagger(soccerPlayer);

        armorStand.setVelocity(newVelocity);
        armorStand.setGravity(false);
    }
}
