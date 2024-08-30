package com.covercorp.holosports.commons.commandcenter.exception;

import com.covercorp.holosports.commons.commandcenter.CommandNode;

import java.io.Serial;

public class CommandSenderException extends CommandFailureException {

    @Serial private static final long serialVersionUID = 1L;

    private final Class<?> requiredType;

    public CommandSenderException(CommandNode<?> command, Class<?> requiredType) {
        super(command);

        this.requiredType = requiredType;
    }

    public Class<?> getRequiredType() {
        return requiredType;
    }

    @Override
    public String getMessage() {
        return "Unsupported command sender. Required type " + requiredType.getSimpleName();
    }
}
