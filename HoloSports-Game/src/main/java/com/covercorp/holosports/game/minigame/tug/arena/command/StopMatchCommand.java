package com.covercorp.holosports.game.minigame.tug.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class StopMatchCommand extends BukkitCommand<CommandSender> {
    private final TugMiniGame tugMiniGame;
    private final TugArena tugArena;

    public StopMatchCommand(final TugArena tugArena) {
        super(CommandSender.class, "tug:stop", "No specific usage, just the command.");

        this.tugMiniGame = tugArena.getTugMiniGame();
        this.tugArena = tugArena;

        this.setPermission("holosports.tug.stop");
        this.setDescription("Stop the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (tugArena.getState() == TugMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't stop the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match is still not started!\n "));
            return;
        }

        sender.sendMessage(ChatColor.RED + "Stopping match...");
        tugArena.stop();
    }
}