package com.covercorp.holosports.game.minigame.potato.arena.task;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public final class MatchWinnerFireworksTask implements Runnable {
    private final PotatoArena potatoArena;

    private final IPotatoTeam winnerTeam;

    public MatchWinnerFireworksTask(final PotatoArena arena, final IPotatoTeam winnerTeam) {
        potatoArena = arena;

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
