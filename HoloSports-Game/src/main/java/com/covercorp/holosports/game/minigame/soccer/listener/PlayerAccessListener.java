package com.covercorp.holosports.game.minigame.soccer.listener;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;

import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public final class PlayerAccessListener implements Listener {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena arena;

    public PlayerAccessListener(SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        arena = soccerMiniGame.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Location location = arena.getLobbyLocation();

        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(location);
        player.getInventory().clear();

        /*
        if (NicePlayersUtil.isNicePlayer(player)) {
            final Inventory inventory = player.getInventory();

            inventory.setItem(4, SoccerGameItemCollection.getStartItem());
            inventory.setItem(5, SoccerGameItemCollection.getResumeItem());
            inventory.setItem(6, SoccerGameItemCollection.getStopItem());
        }*/

        event.setJoinMessage(CommonUtil.colorize("&7&o" + player.getName() + " just moved in game arena!"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.setQuitMessage(CommonUtil.colorize("&7&o" + player.getName() + " left to the hub!"));

        final ISoccerTeamHelper teamHelper = soccerMiniGame.getTeamHelper();

        final Optional<ISoccerPlayer> soccerPlayerOptional = soccerMiniGame.getPlayerHelper().getPlayer(player.getUniqueId());
        if (soccerPlayerOptional.isPresent()) {
            if (arena.getState() == SoccerMatchState.GAME || arena.getState() == SoccerMatchState.PAUSED) {
                final ISoccerTeam team = soccerPlayerOptional.get().getTeam();
                if (team != null) {
                    final ISoccerTeam oppositeTeam = teamHelper.getOppositeTeam(team);
                    if (oppositeTeam != null) {
                        oppositeTeam.setGoals(team.getGoals() + 5);
                        arena.stop();

                        if (soccerPlayerOptional.get().isReferee()) {
                            arena.getArenaAnnouncer().sendGlobalMessage("&c&lThe match has been cancelled due to the referee leaving the game!");
                        } else {
                            arena.getArenaAnnouncer().sendGlobalMessage("&c&lTeam " + oppositeTeam.getName() + " won the match because one member of the other team left the game!");
                        }
                    }
                }
            }
            soccerMiniGame.getPlayerHelper().removePlayer(player.getUniqueId());
        }
    }
}
