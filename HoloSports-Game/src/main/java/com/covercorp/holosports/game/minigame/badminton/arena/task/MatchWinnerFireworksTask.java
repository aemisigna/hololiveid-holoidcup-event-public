package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public final class MatchWinnerFireworksTask implements Runnable {
    private final BadmintonArena badmintonArena;

    private final IBadmintonTeam winnerTeam;

    public MatchWinnerFireworksTask(final BadmintonArena arena, final IBadmintonTeam winnerTeam) {
        badmintonArena = arena;

        this.winnerTeam = winnerTeam;
    }

    @Override
    public void run() {
        winnerTeam.getPlayers().stream().toList().forEach(winner -> {
            final Player player = Bukkit.getPlayer(winner.getUniqueId());
            if (player == null) return;

            final World world = player.getWorld();

            final Firework firework = world.spawn(player.getLocation(), Firework.class);
            final FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.setPower(2);
            fireworkMeta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.GREEN).withFade(Color.YELLOW).build());

            firework.setFireworkMeta(fireworkMeta);
            firework.detonate();

            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8F, 0.8F);
            player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 100, 0.5, 0.5, 0.5, 0.1);
        });
    }
}
