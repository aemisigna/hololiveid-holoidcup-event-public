package com.covercorp.holosports.game.minigame.soccer.util;

import org.bukkit.util.Vector;

public final class VectorUtil {
    public static Vector calculateVelocity(final Vector from, final Vector to, final int heightGain) {
        // Gravity of a potion
        final double gravity = 0.115;

        // Block locations
        final int endGain = to.getBlockY() - from.getBlockY();
        final double horizDist = Math.sqrt(distanceSquared(from, to));

        // Height gain
        final double maxGain = Math.max(heightGain, (endGain + heightGain));

        // Solve quadratic equation for velocity
        final double a = -horizDist * horizDist / (4 * maxGain);
        final double b = horizDist;
        final double c = -endGain;

        double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);

        // Vertical velocity
        double vy = Math.sqrt(maxGain * gravity);

        // Horizontal velocity
        double vh = vy / slope;

        // Calculate horizontal direction
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;

        // Horizontal velocity components
        double vx = vh * dirx;
        double vz = vh * dirz;

        return new Vector(vx, vy, vz);
    }

    private static double distanceSquared(final Vector from, final Vector to) {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();

        return dx * dx + dz * dz;
    }
}
