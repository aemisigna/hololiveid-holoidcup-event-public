package com.covercorp.holosports.commons.commandcenter.adapter.bukkit;

import com.covercorp.holosports.commons.HoloSportsCommons;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.CommandParameters;
import com.covercorp.holosports.commons.commandcenter.CommandRoot;
import com.covercorp.holosports.commons.commandcenter.adapter.AbstractCommandAdapter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BukkitCommandAdapter extends AbstractCommandAdapter<CommandSender> {
    private final CommandListener listener;

    public BukkitCommandAdapter(final BukkitCommand<?> command) {
        super(command);

        this.listener = new CommandListener(command.getCommandIdentifier(), command.getDescription(), command.getUsage(), command.getAliases());
    }

    public void register(final Plugin plugin) {
        //final Plugin plugin = HoloSportsCommons.getCoreCommons();
        final CommandRoot<?> root = this.getCommand();
        final String name = root.getCommandIdentifier();

        try {
            final Server server = Bukkit.getServer();
            final Method commandMapGetter = server.getClass().getMethod("getCommandMap");
            final CommandMap commandMap = (CommandMap) commandMapGetter.invoke(server);

            final PluginCommand exists = Bukkit.getPluginCommand(name);

            if (exists != null) exists.unregister(commandMap);

            commandMap.register(plugin.getDescription().getName(), listener);
        } catch (Exception ex) {
            throw new RuntimeException("se rompió esta wea", ex);
        }
    }

    @Override
    public AbstractCommandSender<CommandSender> abstractSender(CommandSender sender) {
        return new BukkitCommandSender<>(sender);
    }

    @Override
    public CommandParameters getArguments(String[] args, AbstractCommandSender<CommandSender> sender) {
        return BukkitCommandParameters.fromStrings(this.getCommand(), sender, args);
    }

    public class CommandListener extends Command {

        protected CommandListener(final String name, final String description, final String usage, final List<String> aliases) {
            super(name, description, usage, aliases);
        }

        @Override
        public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
            return BukkitCommandAdapter.super.onTabComplete(sender, args);
        }

        @Override
        public boolean execute(final CommandSender sender, final String cmd, final String[] args) {
            BukkitCommandAdapter.super.onCommand(sender, args);
            return true;
        }

        @Override
        public final String getName() {
            return getCommand().getCommandIdentifier();
        }

        @Override
        public final String getUsage() {
            return getCommand().getUsage();
        }

        @Override
        public final String getDescription() {
            final String description = getCommand().getDescription();
            return description != null ? description : "";
        }

        @Override
        public final String getPermission() {
            return getCommand().getPermission();
        }

        @Override
        public List<String> getAliases() {
            return new ArrayList<>(getCommand().getAliases());
        }
    }
}
