package com.covercorp.holosports.game.minigame.potato.player;

import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface IPotatoPlayerHelper {
    IPotatoPlayer addPlayer(final Player player);
    boolean removePlayer(final UUID uniqueId);

    Optional<IPotatoPlayer> getPlayer(final UUID uniqueId);
    Optional<IPotatoPlayer> getOrCreatePlayer(final Player player);

    ImmutableList<IPotatoPlayer> getPlayerList();
    void clearPlayerList();
}
