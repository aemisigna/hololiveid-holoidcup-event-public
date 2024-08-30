package com.covercorp.holosports.game.minigame.badminton.listener;

import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class PlayerStatsListener implements Listener {
    private final BadmintonMiniGame badmintonMiniGame;
    private final BadmintonArena arena;

    public PlayerStatsListener(BadmintonMiniGame badmintonMiniGame) {
        this.badmintonMiniGame = badmintonMiniGame;

        arena = badmintonMiniGame.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHungerChange(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        event.setCancelled(true);
    }
}
