package com.covercorp.holosports.game.minigame.badminton.player;

import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface IBadmintonPlayerHelper {
    IBadmintonPlayer addPlayer(final Player player);
    boolean removePlayer(final UUID uniqueId);

    Optional<IBadmintonPlayer> getPlayer(final UUID uniqueId);
    Optional<IBadmintonPlayer> getOrCreatePlayer(final Player player);

    ImmutableList<IBadmintonPlayer> getPlayerList();
    void clearPlayerList();
}
