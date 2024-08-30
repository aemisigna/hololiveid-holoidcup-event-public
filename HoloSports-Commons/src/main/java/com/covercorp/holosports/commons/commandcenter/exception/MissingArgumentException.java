package com.covercorp.holosports.commons.commandcenter.exception;

import com.covercorp.holosports.commons.commandcenter.CommandNode;

import java.io.Serial;

public class MissingArgumentException extends InvalidArgumentException {
    @Serial private static final long serialVersionUID = 1L;

    private final int index;

    public MissingArgumentException(CommandNode<?> command, Class<?> required, int index) {
        super(command, required, null);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getMessage() {
        return "Expected " + getRequired().getSimpleName() + " at index " + index;
    }
}
