package com.covercorp.holosports.game.minigame.badminton.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
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
    private final BadmintonMiniGame badmintonMiniGame;
    private final BadmintonArena arena;

    public PlayerAccessListener(BadmintonMiniGame badmintonMiniGame) {
        this.badmintonMiniGame = badmintonMiniGame;

        arena = badmintonMiniGame.getArena();
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
            inventory.setItem(5, BadmintonGameItemCollection.getStartItem());
            inventory.setItem(6, BadmintonGameItemCollection.getStopItem());
        }

        player.teleport(location);

        event.setJoinMessage(CommonUtil.colorize("&7&o" + player.getName() + " just moved in game arena!"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Optional<IBadmintonPlayer> badmintonPlayerOptional = badmintonMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (badmintonPlayerOptional.isPresent()) {
            if (badmintonMiniGame.getArena().getState() == BadmintonMatchState.GAME) {
                final IBadmintonTeam team = badmintonPlayerOptional.get().getTeam();
                if (team != null) {
                    final IBadmintonTeam oppositeTeam = badmintonMiniGame.getTeamHelper().getOppositeTeam(team);
                    if (oppositeTeam != null) {
                        oppositeTeam.setPoints(10);
                        arena.stop();

                        arena.getArenaAnnouncer().sendGlobalMessage("&c&lTeam " + oppositeTeam.getName() + " won the match because one member of the other team left the game!");
                    }
                }
            }

            badmintonMiniGame.getPlayerHelper().removePlayer(player.getUniqueId());
        }

        event.setQuitMessage(CommonUtil.colorize("&7&o" + player.getName() + " left to the hub!"));
    }
}
