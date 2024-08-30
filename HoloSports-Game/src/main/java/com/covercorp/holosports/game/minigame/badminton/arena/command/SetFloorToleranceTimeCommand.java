package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class SetFloorToleranceTimeCommand extends BukkitCommand<CommandSender> {
    private final BadmintonArena badmintonArena;

    public SetFloorToleranceTimeCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:floortime", "<time in ticks> (default: 5 ticks) (20 ticks = 1 second)");

        this.badmintonArena = badmintonArena;

        this.setDescription("Set the time that the shuttlecock can be on the floor before it is reset (default: 5 ticks)");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        final int floorToleranceTime = parameters.getInt(0);

        // Set the floor tolerance time.
        badmintonArena.getBadmintonMatchProperties().setFloorToleranceTime(floorToleranceTime);

        sender.sendMessage(ChatColor.YELLOW + "Floor tolerance time: " + ChatColor.AQUA + floorToleranceTime + " ticks");
    }
}
