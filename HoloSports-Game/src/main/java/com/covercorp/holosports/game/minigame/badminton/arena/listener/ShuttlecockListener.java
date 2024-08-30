package com.covercorp.holosports.game.minigame.badminton.arena.listener;

import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.BadmintonBall;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.hit.HitType;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import com.covercorp.holosports.game.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class ShuttlecockListener implements Listener {
    private final BadmintonArena arena;

    public ShuttlecockListener(final BadmintonArena arena) {
        this.arena = arena;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (NBTMetadataUtil.hasEntityString(event.getEntity(), "accessor")) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShuttlecockHit(final EntityDamageByEntityEvent event) {
        final Entity eventEntity = event.getEntity();
        if (!NBTMetadataUtil.hasEntityString(eventEntity, "accessor")) return;

        if (!(event.getDamager() instanceof final Player player)) return;

        if (arena.getState() != BadmintonMatchState.GAME) return;

        final Optional<IBadmintonPlayer> badmintonPlayerOptional = arena.getBadmintonMiniGame().getPlayerHelper().getPlayer(player.getUniqueId());
        if (badmintonPlayerOptional.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You are not participating!");
            return;
        }

        final IBadmintonPlayer badmintonPlayer = badmintonPlayerOptional.get();
        final IBadmintonTeam team = badmintonPlayer.getTeam();
        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not in a team!");
            return;
        }

        if (eventEntity.getType() == EntityType.ARMOR_STAND || eventEntity.getType() == EntityType.SLIME) {
            final BadmintonBall badmintonBall = arena.getBadmintonBall();

            if (badmintonBall == null) return;

            final UUID baseId = badmintonBall.getBallArmorStand().getArmorStand().getUniqueId();
            final UUID hitboxId = badmintonBall.getBallArmorStand().getSlime().getUniqueId();

            ArmorStand armorStand;

            if (eventEntity.getUniqueId() == baseId) {
                armorStand = badmintonBall.getBallArmorStand().getArmorStand();
            } else if (eventEntity.getUniqueId() == hitboxId) {
                armorStand = badmintonBall.getBallArmorStand().getArmorStand();
            } else {
                armorStand = null;
            }

            if (armorStand == null) return;

            final IBadmintonPlayer lastTagger = badmintonBall.getLastTagger();
            if (lastTagger != null && lastTagger.getTeam() == team) {
                //player.sendMessage(ChatColor.RED + "You can't hit the shuttlecock twice in a row!");
                return;
            }

            // Service stuff
            if (badmintonBall.getHitType() == HitType.SERVICE) {
                if (badmintonBall.isServiceHit()) {
                    badmintonBall.setHitType(HitType.GAME);
                } else {
                    badmintonBall.setServiceHit(true);
                }
            }

            // Get a number between 4 and 8, which is the height of the ball
            final int heightBlocks = ThreadLocalRandom.current().nextInt(5, 8 + 1);
            final Location targetLoc = player.getLocation().clone().add(player.getEyeLocation().getDirection().multiply(getBallPower(player.getLocation())));
            arena.shootShuttlecock(badmintonPlayer, badmintonBall, targetLoc, PlayerUtil.isCriticalHit(player), heightBlocks);
        }
    }

    private double getBallPower(final Location location) {
        double maxDistance = 12.5;
        double minDistance = 8;

        double distance = minDistance + (maxDistance - minDistance) * (location.getPitch() + 90.0) / 180.0;

        distance = Math.max(minDistance, Math.min(distance, maxDistance));

        return distance;
    }
}
