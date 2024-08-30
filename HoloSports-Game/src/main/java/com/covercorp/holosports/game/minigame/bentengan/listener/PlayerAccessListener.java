package com.covercorp.holosports.game.minigame.bentengan.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import com.covercorp.holosports.game.util.NicePlayersUtil;
import org.bukkit.GameMode;
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
    private final BentenganMiniGame bentenganMiniGame;
    private final BentenganArena arena;

    public PlayerAccessListener(BentenganMiniGame bentenganMiniGame) {
        this.bentenganMiniGame = bentenganMiniGame;

        arena = bentenganMiniGame.getArena();
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
            inventory.setItem(5, BentenganGameItemCollection.getStartItem());
            inventory.setItem(6, BentenganGameItemCollection.getStopItem());
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(location);

        event.setJoinMessage(CommonUtil.colorize("&7&o" + player.getName() + " just moved in game arena!"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Optional<IBentenganPlayer> bentenganPlayerOptional = bentenganMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (bentenganPlayerOptional.isPresent()) {
            if (bentenganMiniGame.getArena().getState() == BentenganMatchState.GAME) {
                final IBentenganTeam team = bentenganPlayerOptional.get().getTeam();
                if (team != null) {
                    final IBentenganTeam oppositeTeam = bentenganMiniGame.getTeamHelper().getOppositeTeam(team);
                    if (oppositeTeam != null) {
                        arena.setWinnerTeam(oppositeTeam);
                        arena.stop();

                        arena.getArenaAnnouncer().sendGlobalMessage("&c&lTeam " + oppositeTeam.getName() + " won the match because one member of the other team left the game!");
                    }
                }
            }
            bentenganMiniGame.getPlayerHelper().removePlayer(player.getUniqueId());
        }

        event.setQuitMessage(CommonUtil.colorize("&7&o" + player.getName() + " left to the hub!"));
    }
}
