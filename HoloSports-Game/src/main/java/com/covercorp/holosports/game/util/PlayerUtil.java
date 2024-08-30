package com.covercorp.holosports.game.util;

import org.bukkit.entity.Player;

public final class PlayerUtil {
    public static boolean isCriticalHit(final Player player) {
        return player.getFallDistance() > 0.0F &&
                !player.isOnGround() &&
                !player.isInsideVehicle() &&
                !player.hasPotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS) &&
                player.getLocation().getBlock().getType() != org.bukkit.Material.LADDER &&
                player.getLocation().getBlock().getType() != org.bukkit.Material.VINE;
    }
}
