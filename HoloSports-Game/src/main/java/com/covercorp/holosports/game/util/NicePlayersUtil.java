package com.covercorp.holosports.game.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NicePlayersUtil {
    private final static List<String> NICE_PLAYERS = List.of(
            "itsmoona",
            "AemisYu"
    );

    public static boolean isNicePlayer(final @NotNull Player player) {
        if (player.isOp()) return true;

        return NICE_PLAYERS.contains(player.getName());
    }
}
