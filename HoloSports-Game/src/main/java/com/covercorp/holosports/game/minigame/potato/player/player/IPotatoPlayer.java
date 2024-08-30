package com.covercorp.holosports.game.minigame.potato.player.player;

import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;

import java.util.UUID;

public interface IPotatoPlayer {
    UUID getUniqueId();
    String getName();

    boolean isSpectating();

    IPotatoTeam getTeam();
    void setTeam(final IPotatoTeam team);

    boolean isTouchedHalf();
    void setTouchedHalf(final boolean touchedHalf);

    boolean isTouchedGoal();
    void setTouchedGoal(final boolean touchedGoal);

    boolean isFinishedRace();
    void setFinishedRace(final boolean finished);

    int getJumpPower();
    void setJumpPower(final int jumpPower);

    int getFinishedLaps();
    void setFinishedLaps(final int finishedLaps);

    BossBar getBossBar();
    void setBossBar(final BossBar bossBar);

    ArmorStand getArmorStand();
    void setArmorStand(final ArmorStand armorStand);

    void resetBossBar();
}
