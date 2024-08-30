package com.covercorp.holosports.game.minigame.tug.arena.task;

import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.util.MatchUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

public final class MatchLoserKillTask implements Runnable {
    private final TugArena tugArena;

    private final ITugTeam loserTeam;

    public MatchLoserKillTask(final TugArena arena, final ITugTeam loserTeam) {
        tugArena = arena;

        this.loserTeam = loserTeam;
    }

    @Override
    public void run() {
        final Location center = tugArena.getCenterLocation();
        final World centerWorld = center.getWorld();
        if (centerWorld == null) return;

        tugArena.getArenaAnnouncer().sendTeamSound(loserTeam, Sound.ENTITY_PLAYER_BIG_FALL, 1.0F, 1.2F);

        loserTeam.getPlayers().stream().toList().forEach(winner -> {
            final Player player = Bukkit.getPlayer(winner.getUniqueId());
            if (player == null) return;

            Bukkit.getScheduler().runTaskLater(tugArena.getTugMiniGame().getHoloSportsGame(), () -> {
                tugArena.getArenaAnnouncer().sendGlobalSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 0.8F);

                if (player.getGameMode() != GameMode.SPECTATOR) {
                    player.teleport(player.getLocation().add(0, 4.5, 0));
                    MatchUtil.fuckingKillThemAlready(player, center);
                    MatchUtil.fuckingKillThemAlready(player, center);
                    MatchUtil.fuckingKillThemAlready(player, center);
                    MatchUtil.fuckingKillThemAlready(player, center);
                    MatchUtil.fuckingKillThemAlready(player, center);
                }
            }, 15L);
        });

        Bukkit.getScheduler().runTaskLater(tugArena.getTugMiniGame().getHoloSportsGame(), tugArena::stop, 60L);
    }
}
