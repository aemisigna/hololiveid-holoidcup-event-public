package com.covercorp.holosports.game.minigame.bentengan.listener;

import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Optional;

public final class PlayerStatsListener implements Listener {
    private final BentenganMiniGame bentenganMiniGame;
    private final BentenganArena arena;

    public PlayerStatsListener(BentenganMiniGame bentenganMiniGame) {
        this.bentenganMiniGame = bentenganMiniGame;

        arena = bentenganMiniGame.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHungerChange(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        final Optional<IBentenganPlayer> bentenganPlayerOptional = arena.getPlayerHelper().getPlayer(event.getEntity().getUniqueId());
        if (bentenganPlayerOptional.isPresent()) {
            if (arena.getState() == BentenganMatchState.GAME) {
                event.setCancelled(false);
                return;
            }
        }

        player.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        final Optional<IBentenganPlayer> bentenganPlayerOptional = arena.getPlayerHelper().getPlayer(event.getEntity().getUniqueId());
        if (bentenganPlayerOptional.isPresent()) {
            if (arena.getState() == BentenganMatchState.GAME) {
                event.setCancelled(false);
                return;
            }
        }
        
        event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.getEntity().teleport(arena.getLobbyLocation());
        }
    }
}
