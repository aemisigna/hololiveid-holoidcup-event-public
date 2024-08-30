package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.BadmintonBall;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.hitbox.BadmintonBallArmorStand;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Slime;

public final class BadmintonTickTask implements Runnable {
    private BadmintonArena badmintonArena;

    public BadmintonTickTask(final BadmintonArena badmintonArena) {
        this.badmintonArena = badmintonArena;
    }

    @Override
    public void run() {
        if (badmintonArena.getState() != BadmintonMatchState.GAME) return;

        final BadmintonBall badmintonBall = badmintonArena.getBadmintonBall();
        if (badmintonBall == null) return;

        final BadmintonBallArmorStand ballArmorStand = badmintonBall.getBallArmorStand();
        if (ballArmorStand == null) return;

        final ArmorStand armorStand = badmintonBall.getBallArmorStand().getArmorStand();
        if (armorStand == null) return;

        final Slime slime = badmintonBall.getBallArmorStand().getSlime();
        if (slime == null) return;

        if (ballArmorStand.getFallingBlock() != null) {
            if (!ballArmorStand.getFallingBlock().isDead()) {
                slime.teleport(ballArmorStand.getFallingBlock().getLocation().add(0, 0, 0));
            }
            // Move the slime to the falling block location using vectors
            //slime.setVelocity(badmintonBall.getBallArmorStand().getFallingBlock().getLocation().toVector().subtract(slime.getLocation().toVector()).multiply(0.1));
        }

        armorStand.teleport(slime.getLocation().add(0, -0.8, 0));
    }
}
