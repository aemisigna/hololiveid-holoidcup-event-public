package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class StopMatchCommand extends BukkitCommand<CommandSender> {
    private final BadmintonMiniGame badmintonMiniGame;
    private final BadmintonArena badmintonArena;

    public StopMatchCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:stop", "No specific usage, just the command.");

        this.badmintonMiniGame = badmintonArena.getBadmintonMiniGame();
        this.badmintonArena = badmintonArena;

        this.setPermission("holosports.badminton.stop");
        this.setDescription("Stop the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (badmintonArena.getState() == BadmintonMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't stop the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match is still not started!\n "));
            return;
        }

        sender.sendMessage(ChatColor.RED + "Stopping match...");
        badmintonArena.stop();
    }
}