package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ForceEndMatchCommand extends BukkitCommand<CommandSender> {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena soccerArena;

    public ForceEndMatchCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:forceend", "No specific usage, just the command.");

        this.soccerMiniGame = soccerArena.getSoccerMiniGame();
        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.forceend");
        this.setDescription("Force skip to the end of the second half the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.GAME) {
            sender.sendMessage(ChatColor.RED + "The match is not in progress!");
            return;
        }

        soccerArena.setPlayedMatches(2);
        soccerArena.setGameTime(0);

        if (soccerArena.shouldDoPenalties()) {
            soccerArena.pause();
            soccerArena.setPenaltyMode(true);
            soccerArena.getArenaAnnouncer().sendGlobalMessage(" \n&6&lMatch forcefully finished!");
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The game ended in a DRAW. Both teams must play penalties to win.\n ");
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7All teams must have at least 2 participants, otherwise the game will be cancelled whilst resuming.\n ");
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The referee must resume the game to start penalty mode.\n ");
        } else {
            soccerArena.getArenaAnnouncer().sendGlobalMessage(" \n&6&lMatch forcefully finished!");
            soccerArena.getArenaAnnouncer().sendGlobalMessage("&7The game is now finished!\n ");

            soccerArena.stop();
        }
    }
}