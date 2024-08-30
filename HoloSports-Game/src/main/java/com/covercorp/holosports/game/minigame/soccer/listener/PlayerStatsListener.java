package com.covercorp.holosports.game.minigame.soccer.listener;

import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class PlayerStatsListener implements Listener {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena arena;

    public PlayerStatsListener(SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        arena = soccerMiniGame.getArena();
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

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.getEntity().teleport(arena.getLobbyLocation());
        }
    }
}
