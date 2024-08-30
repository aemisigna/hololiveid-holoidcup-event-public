package com.covercorp.holosports.commons.commandcenter.sender;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractCommandSender<T> {
    @Getter(AccessLevel.PUBLIC) protected final T sender;

    public AbstractCommandSender(final T sender) {
        this.sender = sender;
    }

    public abstract void sendMessage(final String message);

    public abstract boolean hasPermission(final String permission);

    public abstract String getName();
}

