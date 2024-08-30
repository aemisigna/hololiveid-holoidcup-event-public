package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ResumeMatchCommand extends BukkitCommand<CommandSender> {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena soccerArena;

    public ResumeMatchCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:resume", "No specific usage, just the command.");

        this.soccerMiniGame = soccerArena.getSoccerMiniGame();
        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.resume");
        this.setDescription("Resume the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.PAUSED) {
            sender.sendMessage(ChatColor.RED + "[!] The match is not paused!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Resuming game...");
        soccerArena.resume();
    }
}