package com.covercorp.holosports.game.minigame.potato.listener;

import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Optional;

public final class PlayerStatsListener implements Listener {
    private final PotatoMiniGame potatoMiniGame;
    private final PotatoArena arena;

    public PlayerStatsListener(PotatoMiniGame potatoMiniGame) {
        this.potatoMiniGame = potatoMiniGame;

        arena = potatoMiniGame.getArena();
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

        final Optional<IPotatoPlayer> potatoPlayerOptional = arena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (potatoPlayerOptional.isPresent()) {
            if (arena.getState() == PotatoMatchState.GAME) {
                event.setCancelled(false);

                event.setDamage(0.01);
                player.setHealth(20.0);
                return;
            }
        }
        
        event.setCancelled(true);
    }
}
