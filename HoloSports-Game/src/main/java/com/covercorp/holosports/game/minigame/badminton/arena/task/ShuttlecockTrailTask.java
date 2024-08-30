package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.BadmintonBall;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public final class ShuttlecockTrailTask implements Runnable {
    private final BadmintonArena badmintonArena;

    public ShuttlecockTrailTask(final BadmintonArena badmintonArena) {
        this.badmintonArena = badmintonArena;
    }

    @Override
    public void run() {
        if (badmintonArena.getState() != BadmintonMatchState.GAME) return;

        final BadmintonBall badmintonBall = badmintonArena.getBadmintonBall();

        if (badmintonBall == null) return;

        if (!badmintonBall.isFlying()) return;

        final ArmorStand armorStand = badmintonBall.getBallArmorStand().getArmorStand();
        if (armorStand == null) return;

        final World world = armorStand.getWorld();
        final Location particleLoc = armorStand.getLocation().add(0, 0.8, 0);

        // Spawn a firework spark at the shuttlecock's location
        world.spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 1, 0, 0, 0, 0);

        if (badmintonBall.isSmashed()) {
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 4, 0, 0.04, 0, -0.04);
        }
        if (badmintonBall.getLastTagger() != null) {
            final IBadmintonTeam team = badmintonBall.getLastTagger().getTeam();
            if (team == null) return;

            final String teamColor = team.getColor();
            final Particle.DustOptions dustOptions = new Particle.DustOptions(teamColor.equals("WHITE") ? Color.RED : Color.BLUE, 1);

            world.spawnParticle(Particle.REDSTONE, particleLoc, 10, 0, 0, 0, dustOptions);
        }
    }
}
