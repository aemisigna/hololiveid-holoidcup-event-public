package com.covercorp.holosports.game.listener;

import com.covercorp.holosports.game.HoloSportsGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class CosmeticJoinListener implements Listener {
    private final HoloSportsGame holoSportsGame;

    public CosmeticJoinListener(final HoloSportsGame holoSportsGame) {
        this.holoSportsGame = holoSportsGame;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.sendTitle(
                "\uE299\uE299\uE299\uE299\uE299\uE299\uE299",
                "",
                0,
                20,
                10
        );
    }
}
