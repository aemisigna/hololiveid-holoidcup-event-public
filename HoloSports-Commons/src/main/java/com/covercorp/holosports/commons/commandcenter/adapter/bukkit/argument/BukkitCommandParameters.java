package com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument;

import com.covercorp.holosports.commons.commandcenter.CommandNode;
import com.covercorp.holosports.commons.commandcenter.CommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.commandcenter.exception.InvalidArgumentException;
import com.covercorp.holosports.commons.commandcenter.exception.MissingArgumentException;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class BukkitCommandParameters extends CommandParameters {

    public static CommandParameters fromStrings(final CommandNode<?> command, final AbstractCommandSender<?> sender, final String[] args) {
        return new BukkitCommandParameters(command, sender, args);
    }

    public BukkitCommandParameters(final CommandNode<?> command, final AbstractCommandSender<?> sender, final String[] arguments) {
        super(command, sender, arguments);
    }

    public Player getPlayer(final int index) throws CommandFailureException {
        if (!isPresent(index)) throw new MissingArgumentException(getCommand(), Player.class, index);

        final String arg = get(index);
        final Player player = Bukkit.getPlayer(arg);

        if (player == null) throw new InvalidArgumentException(getCommand(), Player.class, arg);

        return player;
    }
}
