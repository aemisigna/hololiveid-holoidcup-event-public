package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;

import org.bukkit.command.CommandSender;

public final class SetMatchTimeCommand extends BukkitCommand<CommandSender> {
    private final SoccerArena soccerArena;

    public SetMatchTimeCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:setmatchhalftime", "<time in seconds> (default: 300)");

        this.soccerArena = soccerArena;

        //this.setPermission("holosports.soccer.setmatchhalftime");
        this.setDescription("Sets the match time per half in seconds.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only set the time per half if the game is not started."));
            return;
        }

        final int oldTime = soccerArena.getTimePerHalf();
        final int newTime = parameters.getInt(0);

        soccerArena.setTimePerHalf(newTime);

        final String oldTimeFormatted = String.format("%02d:%02d", oldTime / 60, oldTime % 60);
        final String newTimeFormatted = String.format("%02d:%02d", newTime / 60, newTime % 60);

        final String newMatchTimeFormatted = String.format("%02d:%02d", newTime * 2 / 60, newTime * 2 % 60);

        sender.sendMessage(CommonUtil.colorize("&eThe time per half has been changed from &b" + oldTimeFormatted + " &etime to &b" + newTimeFormatted + "&7."));
        sender.sendMessage(CommonUtil.colorize("&7The TOTAL match time will now be &c" + newMatchTimeFormatted + " &7long. With &c" + newTimeFormatted + " &7time PER HALF."));
    }
}
