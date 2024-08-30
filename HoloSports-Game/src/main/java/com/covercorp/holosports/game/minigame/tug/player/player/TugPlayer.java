package com.covercorp.holosports.game.minigame.tug.player.player;

import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class TugPlayer implements ITugPlayer {
    private final UUID uniqueId;
    private final String name;

    private @Nullable ITugTeam team;

    @Override
    public boolean isSpectating() {
        final Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) return false;

        return player.getGameMode() == GameMode.SPECTATOR;
    }
}
