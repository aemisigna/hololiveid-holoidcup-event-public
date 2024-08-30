package com.covercorp.holosports.game.minigame.potato.player.player;

import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class PotatoPlayer implements IPotatoPlayer {
    private final UUID uniqueId;
    private final String name;

    private @Nullable IPotatoTeam team;

    private int jumpPower = 0;
    private @Nullable BossBar bossBar;

    private @Nullable ArmorStand armorStand;

    private boolean touchedHalf;
    private boolean touchedGoal;

    private int finishedLaps;

    private boolean finishedRace;

    @Override
    public boolean isSpectating() {
        final Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) return false;

        return player.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public void resetBossBar() {
        if (bossBar == null) return;

        bossBar.removeAll();
        bossBar = null;
    }
}
