package com.covercorp.holosports.game.minigame.bentengan.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class StopMatchCommand extends BukkitCommand<CommandSender> {
    private final BentenganMiniGame bentenganMiniGame;
    private final BentenganArena bentenganArena;

    public StopMatchCommand(final BentenganArena bentenganArena) {
        super(CommandSender.class, "bentengan:stop", "No specific usage, just the command.");

        this.bentenganMiniGame = bentenganArena.getBentenganMiniGame();
        this.bentenganArena = bentenganArena;

        this.setPermission("holosports.bentengan.stop");
        this.setDescription("Stop the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (bentenganArena.getState() == BentenganMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't stop the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match is still not started!\n "));
            return;
        }

        sender.sendMessage(ChatColor.RED + "Stopping match...");
        bentenganArena.stop();
    }
}