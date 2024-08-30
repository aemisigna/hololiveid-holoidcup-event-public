package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.ball.SoccerBall;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public final class MatchTickTask implements Runnable {
    private final SoccerArena soccerArena;

    private EulerAngle rotation;

    public MatchTickTask(final SoccerArena arena) {
        soccerArena = arena;

        rotation = new EulerAngle(0.0, 0.0, 0.0);
    }

    @Override
    public void run() {
        startBallMovement();
    }

    private void startBallMovement() {
        final SoccerBall soccerBall = soccerArena.getSoccerBall();
        if (soccerBall == null) return;

        if (soccerBall.getBallArmorStand() == null) return;

        final ArmorStand ballStand = soccerBall.getBallArmorStand().getBaseStand();
        if (ballStand == null) return;

        final Vector velocity = ballStand.getVelocity();

        final EulerAngle baseRotation = ballStand.getHeadPose();

        // Verificar si la pelota está en movimiento
        if (ballStand.getVelocity().length() > 0.1) {
            double newYaw = rotation.getY() + 0.4; // Cambia la velocidad de rotación ajustando el valor aquí
            if (newYaw >= 2 * Math.PI) {
                newYaw = 0;
            }

            rotation = new EulerAngle(newYaw, rotation.getX(), rotation.getZ());

            ballStand.setHeadPose(rotation);
            ballStand.setGravity(true);

            // Spawn an unique fire particle at the ball location
            ballStand.getWorld().spawnParticle(Particle.FLAME, ballStand.getLocation(), 1, 0.1, 0.1, 0.1, 0.01);

            /*
            double length;

            if (ballStand.isOnGround()) {
                length = new Vector(velocity.getX(), 0.0, velocity.getZ()).length();
            } else {
                length = velocity.length();
            }

            //System.out.println(length);
/*
            EulerAngle angle;
            if (length > 1.0) angle = new EulerAngle(0.0, baseRotation.getX() + 30, 0.0);
            else if (length > 0.1) angle = new EulerAngle(0.0, baseRotation.getX() + 10, 0.0);
            else if (length > 0.08) angle = new EulerAngle(0.0, baseRotation.getX() + 5, 0.0);
            else angle = null;

            EulerAngle angle;
            if (length > 1.0) angle = new EulerAngle(baseRotation.getX() + 1.0F, 0.0, 0.0);
            else if (length > 0.1) angle = new EulerAngle(baseRotation.getX() + 2.0F, 0.0, 0.0);
            else if (length > 0.08) angle = new EulerAngle(baseRotation.getX() + 3.0F, 0.0, 0.0);
            else angle = null;

            if (angle == null) return;

            baseRotation = angle;

            ballStand.setHeadPose(baseRotation);
            ballStand.setGravity(true);*/
        }

        /*
        ballStand.setVelocity(new Vector(0.0, 0.0, 0.0));
        //System.out.println("Setting EulerAngle to zero");

        ballStand.setGravity(true);
        ballStand.setHeadPose(EulerAngle.ZERO);

        this.cancel();*/
    }
}
