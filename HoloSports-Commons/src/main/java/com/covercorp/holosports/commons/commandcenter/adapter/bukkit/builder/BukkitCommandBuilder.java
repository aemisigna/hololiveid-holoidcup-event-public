package com.covercorp.holosports.commons.commandcenter.adapter.bukkit.builder;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.executor.BukkitCommandExecutor;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.player.HumanCommand;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class BukkitCommandBuilder {
    private final String name;
    private String prefix = "[HoloSports-Commons]";
    private String hint = "";
    private String permission = null;
    private String[] alias = {};
    private final Map<Integer, Autocompleter<Player>> autocompleters = new HashMap<>();

    private BukkitCommandBuilder(String name) {
        this.name = name;
    }

    public static BukkitCommandBuilder name(String name) {
        return new BukkitCommandBuilder(name);
    }

    public BukkitCommandBuilder prefix(String prefix) {
        this.prefix = prefix;

        return this;
    }

    public BukkitCommandBuilder hint(String hint) {
        this.hint = hint;
        return this;
    }

    public BukkitCommandBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    public BukkitCommandBuilder alias(String... aliases) {
        this.alias = aliases;
        return this;
    }

    public BukkitCommandBuilder autocomplete(final int index, final Autocompleter<Player> autocompleter) {
        this.autocompleters.put(index, autocompleter);

        return this;
    }

    public HumanCommand register(final Plugin plugin, final BukkitCommandExecutor executor) {
        final HumanCommand command = new HumanCommand(name, hint) {
            @Override
            protected void onCommand(Player sender, BukkitCommandParameters args) throws CommandFailureException {
                executor.execute(sender, args);
            }
        };

        for (final Map.Entry<Integer, Autocompleter<Player>> entry : autocompleters.entrySet()) {
            command.setAutocompleter(entry.getKey(), entry.getValue());
        }

        command.setPrefix(prefix);
        command.setPermission(permission);
        command.setAliases(alias);
        command.register(plugin);

        return command;
    }

}