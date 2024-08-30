package com.covercorp.holosports.hub.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.hub.HoloSportsHub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class HubListener implements Listener {
    private final HoloSportsHub holoSportsHub;

    public HubListener(final HoloSportsHub holoSportsHub) {
        this.holoSportsHub = holoSportsHub;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.teleport(holoSportsHub.getConfigHelper().getSpawnLocation());
        player.sendMessage(CommonUtil.colorize(holoSportsHub.getConfigHelper().getMessageOfTheDay()));

        player.sendTitle(
                "\uE299\uE299\uE299\uE299\uE299\uE299\uE299",
                "",
                0,
                20,
                10
        );

        event.setJoinMessage(CommonUtil.colorize("&7&o" + player.getName() + " just moved in the hub!"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHungerChange(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.setFoodLevel(20);
        event.setCancelled(true);
    }
}
