package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class StopMatchCommand extends BukkitCommand<CommandSender> {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena soccerArena;

    public StopMatchCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:stop", "No specific usage, just the command.");

        this.soccerMiniGame = soccerArena.getSoccerMiniGame();
        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.stop");
        this.setDescription("Stop the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() == SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't stop the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match is still not started!\n "));
            return;
        }

        sender.sendMessage(ChatColor.RED + "Stopping match...");
        soccerArena.stop();
    }
}