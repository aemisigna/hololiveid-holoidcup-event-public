package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonGameType;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.BadmintonBall;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public final class ShuttlecockZoneDebugTask implements Runnable {
    private final BadmintonArena badmintonArena;

    public ShuttlecockZoneDebugTask(final BadmintonArena badmintonArena) {
        this.badmintonArena = badmintonArena;
    }

    @Override
    public void run() {
        if (badmintonArena.getState() != BadmintonMatchState.GAME) return;
        if (!badmintonArena.getBadmintonMatchProperties().isParticleMode()) return;

        final BadmintonBall badmintonBall = badmintonArena.getBadmintonBall();

        if (badmintonBall == null) return;

        if (!badmintonBall.isFlying()) return;

        final ArmorStand armorStand = badmintonBall.getBallArmorStand().getArmorStand();
        if (armorStand == null) return;

        if (badmintonBall.getLastTagger() != null) {
            final IBadmintonTeam team = badmintonBall.getLastTagger().getTeam();
            if (team == null) return;

            final Cuboid cuboid = badmintonArena.getGoalZone(team);
            if (cuboid != null) {
                cuboid.getBlockList().forEachRemaining(block -> {
                    final Location blockLoc = block.getLocation();

                    blockLoc.getWorld().spawnParticle(Particle.REDSTONE, blockLoc, 1, 0, 0, 0, new Particle.DustOptions(Color.RED, 2));
                });
            }
        }
    }
}
