package com.covercorp.holosports.game.minigame.tug.player;

import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface ITugPlayerHelper {
    ITugPlayer addPlayer(final Player player);
    boolean removePlayer(final UUID uniqueId);

    Optional<ITugPlayer> getPlayer(final UUID uniqueId);
    Optional<ITugPlayer> getOrCreatePlayer(final Player player);

    ImmutableList<ITugPlayer> getPlayerList();
    void clearPlayerList();
}
