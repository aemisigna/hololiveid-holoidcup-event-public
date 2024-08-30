package com.covercorp.holosports.game.minigame.potato.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class StopMatchCommand extends BukkitCommand<CommandSender> {
    private final PotatoMiniGame potatoMiniGame;
    private final PotatoArena potatoArena;

    public StopMatchCommand(final PotatoArena potatoArena) {
        super(CommandSender.class, "potato:stop", "No specific usage, just the command.");

        this.potatoMiniGame = potatoArena.getPotatoMiniGame();
        this.potatoArena = potatoArena;

        this.setPermission("holosports.potato.stop");
        this.setDescription("Stop the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (potatoArena.getState() == PotatoMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't stop the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match is still not started!\n "));
            return;
        }

        sender.sendMessage(ChatColor.RED + "Stopping match...");
        potatoArena.stop();
    }
}