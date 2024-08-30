package com.covercorp.holosports.game.minigame.soccer.ball.task;

import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import com.covercorp.holosports.game.minigame.soccer.ball.stand.BallArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;


public final class GravityTask implements Runnable {
    private SoccerBall soccerBall;

    private final double floorPos = 65.0;

    private double incrementMultiplier = 0;

    public GravityTask(SoccerBall soccerBall) {
        this.soccerBall = soccerBall;
    }
    @Override
    public void run() {
        final BallArmorStand ballArmorStand = soccerBall.getBallArmorStand();

        if (ballArmorStand == null) return;

        final ArmorStand armorStand = soccerBall.getBallArmorStand().getBaseStand();

        if (armorStand.getLocation().getY() <= floorPos) return;

        if (armorStand.getLocation().getY() == floorPos + 1.0) {
            //armorStand.setGravity(false);

            final Location l = armorStand.getLocation().clone();
            l.setY(66);

            //System.out.println("Teleporting...");

            //armorStand.teleport(l);

            //return;
        }

        final Vector vector = new Vector(0, -0.1, 0);

        armorStand.setVelocity(armorStand.getVelocity().add(vector));
    }
}
