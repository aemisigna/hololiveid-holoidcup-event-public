package com.covercorp.holosports.game.minigame.potato.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.inventory.PotatoGameItemCollection;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import com.covercorp.holosports.game.util.NicePlayersUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public final class PlayerAccessListener implements Listener {
    private final PotatoMiniGame potatoMiniGame;
    private final PotatoArena arena;

    public PlayerAccessListener(PotatoMiniGame potatoMiniGame) {
        this.potatoMiniGame = potatoMiniGame;

        arena = potatoMiniGame.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Location location = arena.getLobbyLocation();
        final Inventory inventory = player.getInventory();

        inventory.clear();

        if (player.getEquipment() != null) {
            player.getEquipment().clear();
        }

        if (NicePlayersUtil.isNicePlayer(player)) {
            inventory.setItem(5, PotatoGameItemCollection.getStartItem());
            inventory.setItem(6, PotatoGameItemCollection.getStopItem());
        }

        player.teleport(location);

        event.setJoinMessage(CommonUtil.colorize("&7&o" + player.getName() + " just moved in game arena!"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Optional<IPotatoPlayer> potatoPlayerOptional = potatoMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (potatoPlayerOptional.isPresent()) {
            if (potatoMiniGame.getArena().getState() == PotatoMatchState.GAME) {
                final IPotatoTeam team = potatoPlayerOptional.get().getTeam();
                if (team != null) {
                    final IPotatoTeam oppositeTeam = potatoMiniGame.getTeamHelper().getOppositeTeam(team);
                    if (oppositeTeam != null) {
                        arena.setWinnerTeam(oppositeTeam);
                        arena.stop();

                        arena.getArenaAnnouncer().sendGlobalMessage("&c&lTeam " + oppositeTeam.getName() + " won the match because one member of the other team left the game!");
                    }
                }
            }
            
            potatoMiniGame.getPlayerHelper().removePlayer(player.getUniqueId());
        }

        event.setQuitMessage(CommonUtil.colorize("&7&o" + player.getName() + " left to the hub!"));
    }
}
