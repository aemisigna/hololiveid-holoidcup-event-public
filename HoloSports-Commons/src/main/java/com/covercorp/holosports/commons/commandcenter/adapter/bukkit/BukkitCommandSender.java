package com.covercorp.holosports.commons.commandcenter.adapter.bukkit;

import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import org.bukkit.command.CommandSender;

public class BukkitCommandSender<T extends CommandSender> extends AbstractCommandSender<T> {
    public BukkitCommandSender(final T sender) {
        super(sender);
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public String getName() {
        return sender.getName();
    }
}