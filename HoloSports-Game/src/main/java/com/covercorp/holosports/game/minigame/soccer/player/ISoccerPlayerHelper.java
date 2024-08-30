package com.covercorp.holosports.game.minigame.soccer.player;

import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface ISoccerPlayerHelper {
    ISoccerPlayer addPlayer(final Player player);
    boolean removePlayer(final UUID uniqueId);

    Optional<ISoccerPlayer> getPlayer(final UUID uniqueId);
    Optional<ISoccerPlayer> getOrCreatePlayer(final Player player);

    ImmutableList<ISoccerPlayer> getPlayerList();
    void clearPlayerList();
    ImmutableList<ISoccerPlayer> noRolePlayers();
}
