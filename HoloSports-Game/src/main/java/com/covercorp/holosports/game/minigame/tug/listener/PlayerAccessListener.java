package com.covercorp.holosports.game.minigame.tug.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.inventory.TugGameItemCollection;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
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
    private final TugMiniGame tugMiniGame;
    private final TugArena arena;

    public PlayerAccessListener(TugMiniGame tugMiniGame) {
        this.tugMiniGame = tugMiniGame;

        arena = tugMiniGame.getArena();
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
            inventory.setItem(5, TugGameItemCollection.getStartItem());
            inventory.setItem(6, TugGameItemCollection.getStopItem());
        }

        player.teleport(location);

        event.setJoinMessage(CommonUtil.colorize("&7&o" + player.getName() + " just moved in game arena!"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Optional<ITugPlayer> tugPlayerOptional = tugMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (tugPlayerOptional.isPresent()) {
            if (tugMiniGame.getArena().getState() == TugMatchState.GAME) {
                final ITugTeam team = tugPlayerOptional.get().getTeam();
                if (team != null) {
                    final ITugTeam oppositeTeam = tugMiniGame.getTeamHelper().getOppositeTeam(team);
                    if (oppositeTeam != null) {
                        arena.runLoser();

                        arena.getArenaAnnouncer().sendGlobalMessage("&c&lTeam " + oppositeTeam.getName() + " won the match because one member of the other team left the game!");
                    }
                }
            }

            tugMiniGame.getPlayerHelper().removePlayer(player.getUniqueId());
        }

        event.setQuitMessage(CommonUtil.colorize("&7&o" + player.getName() + " left to the hub!"));
    }
}
