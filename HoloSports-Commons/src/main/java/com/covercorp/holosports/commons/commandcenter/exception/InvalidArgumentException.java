package com.covercorp.holosports.commons.commandcenter.exception;

import com.covercorp.holosports.commons.commandcenter.CommandNode;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

public class InvalidArgumentException extends CommandFailureException {
    @Serial private static final long serialVersionUID = 1L;

    private final Class<?> required;
    @Nullable private final String provided;

    public InvalidArgumentException(CommandNode<?> command, Class<?> required, @Nullable String provided) {
        super(command);

        this.required = required;
        this.provided = provided;
    }

    public Class<?> getRequired() {
        return required;
    }

    @Nullable
    public String getProvided() {
        return provided;
    }

    @Override
    public String getMessage() {
        return "Expected type " + required.getSimpleName() + " but got '" + provided + "'";
    }
}
