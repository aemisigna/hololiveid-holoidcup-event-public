package com.covercorp.holosports.game.minigame.soccer.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

public final class EffectUtil {
    public static void playShootEffect(final Location location) {
        final World world = location.getWorld();
        if (world == null) return;

        // Spawn some flame particles at the location to all nearby players on a 20 block ratio
        location.getWorld().spawnParticle(Particle.SMALL_FLAME, location, 50, 0.5, 0.5, 0.5, 0.01);

        // Play a sound at the location to all nearby players on a 20 block ratio
        location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.7F, 0.5F);
    }
}
