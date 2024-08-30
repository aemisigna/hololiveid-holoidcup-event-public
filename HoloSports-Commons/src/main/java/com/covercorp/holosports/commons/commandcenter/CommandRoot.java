package com.covercorp.holosports.commons.commandcenter;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.commandcenter.exception.CommandPermissionException;
import com.covercorp.holosports.commons.commandcenter.exception.InvalidArgumentException;
import com.covercorp.holosports.commons.commandcenter.exception.MissingArgumentException;
import com.covercorp.holosports.commons.commandcenter.handler.CommandFailureHandler;
import com.covercorp.holosports.commons.commandcenter.handler.DefaultCommandFailureHandler;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.shared.util.CollectionUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class CommandRoot<T> extends CommandNode<T> {

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private Autocompleter<T> defaultAutocompleter;
    private CommandFailureHandler<T> failHandler;
    private String prefix;
    private final CommandContext context;

    public CommandRoot(Class<T> senderType, String name, String hint) {
        super(senderType, name, hint);
        this.failHandler = DefaultCommandFailureHandler.getDefault();
        this.context = new CommandContext(this);
        this.prefix = "[HoloSports]";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void executeCommand(final AbstractCommandSender sender, final CommandParameters args) {
        if (!getSenderType().isInstance(sender.getSender())) {
            this.failHandler.onUnsupportedCommandSender(sender, this, getSenderType());
            return;
        }

        final AbstractCommandSender<T> abstractSender = (AbstractCommandSender<T>) sender;

        try {
            super.executeCommand(abstractSender, args);
        } catch (MissingArgumentException ex) {
            failHandler.onMissingArgument(abstractSender, ex.getCommand(), ex.getRequired(), ex.getIndex());
        } catch (InvalidArgumentException ex) {
            failHandler.onInvalidArgument(abstractSender, ex.getCommand(), ex.getRequired(), ex.getProvided());
        } catch (CommandPermissionException ex) {
            failHandler.onPermissionFail(abstractSender, ex.getCommand());
        } catch (CommandFailureException ex) {
            failHandler.onCommandFail(abstractSender, ex.getCommand(), ex.getMessage());
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<String> autocomplete(final AbstractCommandSender sender, final String[] args) throws IllegalArgumentException {
        if (!getSenderType().isInstance(sender.getSender())) {
            return Collections.emptyList();
        }

        final AbstractCommandSender<T> abstractSender = (AbstractCommandSender<T>) sender;

        List<String> suggestions = super.autocomplete(sender, args);

        /* Fallback to default tab completer */
        if (suggestions == null) {
            if (defaultAutocompleter != null) {
                Collection<String> defaultSuggestions = defaultAutocompleter.autocomplete(abstractSender, args[args.length - 1]);
                suggestions = CollectionUtil.toList(defaultSuggestions);
            } else {
                suggestions = Collections.emptyList();
            }
        }
        return suggestions;
    }

    public CommandFailureHandler<T> getFailHandler() {
        return failHandler;
    }

    public void setFailHandler(CommandFailureHandler<T> failHandler) {
        this.failHandler = failHandler;
    }

    @Override
    public String getAbsoluteName() {
        return "/" + getCommandIdentifier();
    }

    public void setPrefix(String prefix) {
        Preconditions.checkNotNull(prefix, "prefix must not be null");
        this.prefix = prefix;
    }

    @Override
    protected CommandContext context() {
        return this.context;
    }
}
