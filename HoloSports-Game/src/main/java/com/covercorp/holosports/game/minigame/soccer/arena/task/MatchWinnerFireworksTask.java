package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public final class MatchWinnerFireworksTask implements Runnable {
    private final SoccerArena soccerArena;

    private final ISoccerTeam winnerTeam;

    public MatchWinnerFireworksTask(final SoccerArena arena, final ISoccerTeam winnerTeam) {
        soccerArena = arena;

        this.winnerTeam = winnerTeam;
    }

    @Override
    public void run() {
        winnerTeam.getPlayers().stream().filter(p -> !p.isReferee()).toList().forEach(winner -> {
            final Player player = Bukkit.getPlayer(winner.getUniqueId());
            if (player == null) return;

            final World world = player.getWorld();

            final Firework firework = world.spawn(player.getLocation(), Firework.class);
            final FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).build());

            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8F, 0.8F);
            player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 100, 0.5, 0.5, 0.5, 0.1);
        });
    }
}
