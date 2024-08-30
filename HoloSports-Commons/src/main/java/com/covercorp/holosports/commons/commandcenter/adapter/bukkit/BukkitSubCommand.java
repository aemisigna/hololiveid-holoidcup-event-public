package com.covercorp.holosports.commons.commandcenter.adapter.bukkit;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.CommandChild;
import com.covercorp.holosports.commons.commandcenter.CommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import org.bukkit.command.CommandSender;

public abstract class BukkitSubCommand<T extends CommandSender> extends CommandChild<T> {
    public BukkitSubCommand(final Class<T> senderType, final String name, final String hint) {
        super(senderType, name, hint);
    }

    @Override
    protected void onCommand(final AbstractCommandSender<T> sender, final CommandParameters args) throws CommandFailureException {
        onCommand(sender.getSender(), (BukkitCommandParameters) args);
    }

    public abstract void onCommand(final T sender, final BukkitCommandParameters args) throws CommandFailureException;
}