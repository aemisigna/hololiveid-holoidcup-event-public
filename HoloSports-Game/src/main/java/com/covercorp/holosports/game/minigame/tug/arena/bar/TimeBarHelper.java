package com.covercorp.holosports.game.minigame.tug.arena.bar;

import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.player.ITugPlayerHelper;
import com.covercorp.holosports.game.minigame.tug.team.ITugTeamHelper;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.util.MatchUtil;
import com.covercorp.holosports.game.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public final class TimeBarHelper {
    private final TugArena tugArena;
    private final ITugPlayerHelper playerHelper;
    private final ITugTeamHelper teamHelper;

    private final BossBar bossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);

    private int timeRemaining;
    private int bossbarTask;
    private int timerTask;

    public TimeBarHelper(final TugArena tugArena) {
        this.tugArena = tugArena;

        this.playerHelper = tugArena.getPlayerHelper();
        this.teamHelper = tugArena.getTeamHelper();
    }

    public void start() {
        timeRemaining = tugArena.getGameTime() * 20;

        bossBar.setTitle(StringUtils.translate("Tug of War"));

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        timerTask = Bukkit.getScheduler().runTaskTimer(tugArena.getTugMiniGame().getHoloSportsGame(), () -> timeRemaining -= 20, 0L, 20L).getTaskId();

        bossbarTask = Bukkit.getScheduler().runTaskTimer(tugArena.getTugMiniGame().getHoloSportsGame(), () -> {
            final ITugTeam team1 = teamHelper.getTeamList().get(0);
            final ITugTeam team2 = teamHelper.getTeamList().get(1);

            double time = (double) timeRemaining / 6000D;

            if (time >= 0.0 && time <= 1.0) {
                bossBar.setTitle(StringUtils.translate(
                        ChatColor.valueOf(team1.getColor()) + String.valueOf(team1.getPoints()) +
                        " &7[ " + MatchUtil.getVersusBar(team1, team2) + " &7] " +
                        ChatColor.valueOf(team2.getColor()) + String.valueOf(team2.getPoints())
                ));
                bossBar.setProgress(time);
            } else {
                Bukkit.getScheduler().cancelTask(bossbarTask);
            }

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);
            });
        }, 10L, 10L).getTaskId();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(timerTask);
        Bukkit.getScheduler().cancelTask(bossbarTask);

        bossBar.removeAll();
    }
}
