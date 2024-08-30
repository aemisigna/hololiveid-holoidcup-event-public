package com.covercorp.holosports.commons.commandcenter.exception;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.covercorp.holosports.commons.commandcenter.CommandNode;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CommandFailureException extends Exception {
    @Serial private static final long serialVersionUID = 1L;

    private final CommandNode<?> command;

    @Nullable private final String reason;

    public CommandFailureException(CommandNode<?> command) {
        this(command, null);
    }

    public <T> CommandNode<T> getCommand() {
        return (CommandNode<T>) command;
    }

    @Override
    public String getMessage() {
        return reason == null ? "[HoloSports-Commons] Failed executing command" : reason;
    }
}
