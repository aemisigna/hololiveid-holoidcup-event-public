package com.covercorp.holosports.commons.commandcenter;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CommandContext {

    private final CommandRoot<?> root;
    private final Map<Class<?>, Object> contextMap = new HashMap<>();

    public <T> void addContext(Class<T> type, T context) throws IllegalStateException {
        if (hasContext(type)) throw new IllegalStateException("command already has context of type " + type.getName());

        contextMap.put(type, context);
    }

    public boolean hasContext(Class<?> type) {
        return contextMap.containsKey(type);
    }

    public <T> T getContext(Class<T> type) throws IllegalArgumentException {
        final Object ctx = contextMap.get(type);
        if (ctx == null) throw new IllegalArgumentException("no context of type " + type.getName() + " found in command '" + root.getCommandIdentifier() + "'");

        return (T) ctx;
    }
}