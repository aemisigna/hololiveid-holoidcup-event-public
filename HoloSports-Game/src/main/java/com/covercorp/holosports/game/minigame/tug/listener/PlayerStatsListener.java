package com.covercorp.holosports.game.minigame.tug.listener;

import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Optional;

public final class PlayerStatsListener implements Listener {
    private final TugMiniGame tugMiniGame;
    private final TugArena arena;

    public PlayerStatsListener(TugMiniGame tugMiniGame) {
        this.tugMiniGame = tugMiniGame;

        arena = tugMiniGame.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHungerChange(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;

        final Optional<ITugPlayer> tugPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (tugPlayerOptional.isPresent()) {
            if (arena.getState() == TugMatchState.GAME) {
                event.setCancelled(false);
                return;
            }
        }

        event.setCancelled(true);
    }
}
