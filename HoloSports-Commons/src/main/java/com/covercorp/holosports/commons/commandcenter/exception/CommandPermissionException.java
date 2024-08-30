package com.covercorp.holosports.commons.commandcenter.exception;

import com.covercorp.holosports.commons.commandcenter.CommandNode;

import java.io.Serial;

public class CommandPermissionException extends CommandFailureException {
    @Serial private static final long serialVersionUID = 1L;

    public CommandPermissionException(final CommandNode<?> command) {
        super(command);
    }

    @Override
    public String getMessage() {
        return "Missing permission " + getCommand().getPermission();
    }
}