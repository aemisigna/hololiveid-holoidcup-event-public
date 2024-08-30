package com.covercorp.holosports.commons.commandcenter.adapter.bukkit;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.CommandParameters;
import com.covercorp.holosports.commons.commandcenter.CommandRoot;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocomplete;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public abstract class BukkitCommand<T extends CommandSender> extends CommandRoot<T> {
    private final BukkitCommandAdapter adapter;

    public BukkitCommand(Class<T> type, String name, String hint) {
        super(type, name, hint);

        adapter = new BukkitCommandAdapter(this);
        setDefaultAutocompleter(Autocomplete.none());
    }

    public void register(final Plugin plugin) {
        adapter.register(plugin);
    }

    @Override
    protected void onCommand(final AbstractCommandSender<T> sender, CommandParameters args) throws CommandFailureException {
        onCommand(sender.getSender(), (BukkitCommandParameters) args);
    }

    protected abstract void onCommand(T sender, BukkitCommandParameters parameters) throws CommandFailureException;
}
