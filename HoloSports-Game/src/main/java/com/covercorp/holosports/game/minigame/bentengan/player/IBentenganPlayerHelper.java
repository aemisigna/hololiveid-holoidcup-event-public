package com.covercorp.holosports.game.minigame.bentengan.player;

import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface IBentenganPlayerHelper {
    IBentenganPlayer addPlayer(final Player player);
    boolean removePlayer(final UUID uniqueId);

    Optional<IBentenganPlayer> getPlayer(final UUID uniqueId);
    Optional<IBentenganPlayer> getOrCreatePlayer(final Player player);

    ImmutableList<IBentenganPlayer> getPlayerList();
    void clearPlayerList();
}
