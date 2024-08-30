package com.covercorp.holosports.game.minigame.soccer.arena.task;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class MatchTimeTask implements Runnable {
    final SoccerArena soccerArena;

    private final ISoccerPlayerHelper playerHelper;

    public MatchTimeTask(final SoccerArena arena) {
        this.soccerArena = arena;

        playerHelper = arena.getSoccerMiniGame().getPlayerHelper();
    }

    @Override
    public void run() {
        if (soccerArena.getState() == SoccerMatchState.WAITING) return;

        if (soccerArena.getState() == SoccerMatchState.PAUSED) return;

        soccerArena.setGameTime(soccerArena.getGameTime() + 1);

        if (soccerArena.getGameTime() == soccerArena.getTimePerHalf()) {
            if (soccerArena.getPlayedMatches() == 0) { // Finished the first match
                soccerArena.setPlayedMatches(1);
                soccerArena.setGameTime(0);

                soccerArena.getArenaAnnouncer().sendGlobalMessage(" \n&6&lFirst half finished! Break time!");
                soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The referee must continue the game!\n ");

                soccerArena.pause();

                return;
            }
            if (soccerArena.getPlayedMatches() == 1) { // Finished the second match
                soccerArena.setPlayedMatches(2);
                soccerArena.setGameTime(0);

                if (soccerArena.shouldDoPenalties()) {
                    soccerArena.pause();
                    soccerArena.setPenaltyMode(true);
                    soccerArena.getArenaAnnouncer().sendGlobalMessage(" \n&6&lSecond half finished!");
                    soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The game ended in a DRAW. Both teams must play penalties to win.\n ");
                    soccerArena.getArenaAnnouncer().sendGlobalMessage("&7All teams must have at least 2 participants, otherwise the game will be cancelled whilst resuming.\n ");
                    soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The referee must resume the game to start penalty mode.\n ");
                } else {
                    soccerArena.getArenaAnnouncer().sendGlobalMessage(" \n&6&lSecond half finished!");
                    soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The game is now finished!\n ");

                    soccerArena.stop();
                }

                return;
            }
        }

        // Send a message the last 5 seconds of the game time
        if (soccerArena.getTimePerHalf() - soccerArena.getGameTime() <= 5 && soccerArena.getTimePerHalf() - soccerArena.getGameTime() >= 0) {
            playerHelper.getPlayerList().forEach(soccerPlayer -> {
                final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
                if (player == null) return;

                player.sendMessage(CommonUtil.colorize("&eThe match half will end in &b" + (soccerArena.getTimePerHalf() - soccerArena.getGameTime()) + " &esecond(s)!"));
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
            });
        }
    }
}
