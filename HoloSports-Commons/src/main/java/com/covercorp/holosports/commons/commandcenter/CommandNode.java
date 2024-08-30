package com.covercorp.holosports.commons.commandcenter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.commandcenter.exception.CommandSenderException;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.commons.commandcenter.exception.CommandPermissionException;
import com.covercorp.holosports.commons.commandcenter.visibility.CommandVisibility;

import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter(AccessLevel.PUBLIC)
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class CommandNode<T> {
    private final String commandIdentifier; // name
    private final String commandHint; // hint

    private final List<String> aliases; // user must specify aliases if they want to use them

    private final Class<T> senderType;

    private final Map<Integer, Collection<CommandChild<? extends T>>> subCommandMap = new HashMap<>();
    private final Map<Integer, Autocompleter<? super T>> autocompleters = new HashMap<>();

    @Setter(AccessLevel.PUBLIC) private CommandVisibility visibility;

    @Setter(AccessLevel.PUBLIC) @Nullable private String permission;
    @Setter(AccessLevel.PUBLIC) @Nullable private String description;

    public CommandNode(final Class<T> senderType, final String commandIdentifier, final String commandHint) {
        this.commandIdentifier = commandIdentifier;
        this.commandHint = commandHint == null ? "" : commandHint;

        this.senderType = senderType;

        this.aliases = new ArrayList<>();

        this.visibility = CommandVisibility.SHOW_ALL;

        this.permission = permission;
        this.description = description;
    }

    protected void executeCommand(final AbstractCommandSender<T> sender, final CommandParameters args) throws CommandFailureException {
        if (!this.hasPermission(sender)) {
            throw new CommandPermissionException(this);
        }

        for (int i = 0; i < args.length(); i++) {
            final String qualifier = args.get(i); // the name of the sub command
            final CommandChild<? extends T> subCommand = getSubCommand(i, qualifier);

            if (subCommand != null) {
                final Class<? extends T> targetType = subCommand.getSenderType();

                if (!targetType.isInstance(sender.getSender())) throw new CommandSenderException(subCommand, targetType);

                subCommand.executeCommand((AbstractCommandSender) sender, args.next(subCommand, i + 1));

                return;
            }
        }

        onCommand(sender, args);
    }

    @Nullable
    protected List<String> autocomplete(AbstractCommandSender<T> sender, String[] args) throws IllegalArgumentException {
        if (args.length == 0) throw new IllegalArgumentException("Cannot autocomplete empty args");
        if (!this.hasPermission(sender)) return Collections.emptyList();

        final List<String> completions = new LinkedList<>();
        final int index = args.length - 1;
        final String currentArg = args[index].toLowerCase();

        // Find subcommand to delegate to
        for (int i = 0; i < args.length - 1; i++) {
            final String qualifier = args[i];
            final CommandChild<? extends T> subCommand = getSubCommand(i, qualifier);

            if (subCommand != null) {
                final String[] argsCopy = Arrays.copyOfRange(args, i + 1, args.length);
                final Class<? extends T> targetType = subCommand.getSenderType();

                if (!targetType.isInstance(sender.getSender())) return Collections.emptyList();

                return subCommand.autocomplete((AbstractCommandSender) sender, argsCopy);
            }
        }

        // Autocomplete subcommand names
        if (subCommandMap.containsKey(index)) {
            for (final CommandChild<?> child : subCommandMap.get(index)) {
                if (child.isAutocompleteVisible()) {
                    if (child.getCommandIdentifier().toLowerCase().startsWith(currentArg)) {
                        if (child.hasPermission(sender)) {
                            completions.add(child.getCommandIdentifier());
                        }
                    }
                }
            }
        }

        /* Return null to fall back to the roots default autocompleter */
        if (this.autocompleters.isEmpty()) return completions.isEmpty() ? null : completions;

        final Autocompleter completer = getAutocompleter(index);

        if (completer != null) completions.addAll(completer.autocomplete(sender, currentArg));

        return completions;
    }

    public void setAutocompleter(int index, Autocompleter<? super T> autocompleter) {
        if (autocompleter == null) {
            autocompleters.remove(index);
        } else {
            autocompleters.put(index, autocompleter);
        }
    }

    private Autocompleter<? super T> getAutocompleter(final int index) {
        return autocompleters.get(index);
    }

    public void addSubCommand(final CommandChild<? extends T> subCommand) {
        addSubCommand(0, subCommand);
    }

    public void addSubCommand(final int index, final CommandChild<? extends T> subCommand) {
        subCommand.setParent(this);

        final Collection<CommandChild<? extends T>> sub = subCommandMap.computeIfAbsent(index, k -> new LinkedList<>());

        sub.add(subCommand);
    }

    @Nullable
    private CommandChild<? extends T> getSubCommand(final int index, final String name) {
        final Collection<CommandChild<? extends T>> sub = subCommandMap.get(index);

        if (sub == null) return null;

        for (final CommandChild<? extends T> command : sub) {
            if (command.getCommandIdentifier().equalsIgnoreCase(name)) return command;

            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(name)) return command;
            }
        }
        return null;
    }

    public void setAliases(final String... aliases) {
        this.aliases.clear();
        this.aliases.addAll(List.of(aliases));
    }

    public void addAlias(final String alias) {
        this.aliases.add(alias);
    }

    public void addAliases(final String... aliasArr) {
        aliases.addAll(List.of(aliasArr));
    }

    public boolean hasPermission(final AbstractCommandSender<?> sender) {
        return permission == null || sender.hasPermission(permission);
    }

    public boolean isAutocompleteVisible() {
        return visibility.isShowAutocomplete();
    }

    public boolean isHelpVisible() {
        return visibility.isShowInHelp();
    }

    public Collection<CommandChild<? extends T>> getSubCommands(final int index) {
        if (subCommandMap.containsKey(index)) return new ArrayList<>(subCommandMap.get(index));

        return Collections.emptyList();
    }

    public String getUsage() {
        final String hint = getCommandHint();

        return getAbsoluteName() + (hint.length() > 0 ? " " + hint : "");
    }

    public <C> void setContext(final Class<C> type, final C context) throws IllegalStateException {
        context().addContext(type, context);
    }

    public <C> C getContext(final Class<C> type) throws IllegalArgumentException {
        return context().getContext(type);
    }

    public abstract String getAbsoluteName();

    protected abstract CommandContext context();

    protected abstract void onCommand(final AbstractCommandSender<T> sender, final CommandParameters args) throws CommandFailureException;
}
