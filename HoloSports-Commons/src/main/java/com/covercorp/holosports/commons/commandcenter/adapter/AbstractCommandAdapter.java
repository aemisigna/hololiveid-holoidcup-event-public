package com.covercorp.holosports.commons.commandcenter.adapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.covercorp.holosports.commons.commandcenter.CommandParameters;
import com.covercorp.holosports.commons.commandcenter.CommandRoot;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class AbstractCommandAdapter<T> {
    @Getter(AccessLevel.PUBLIC) private final CommandRoot<?> command;

    public void onCommand(final T sender, final String[] args) {
        final AbstractCommandSender<T> abstractSender = abstractSender(sender);

        try {
            command.executeCommand(abstractSender, getArguments(args, abstractSender));
        } catch (Exception ex) {
            ex.printStackTrace();
            abstractSender.sendMessage("§c[HoloSports-Commons] An internal error has occurred whilst executing this command.");
        }
    }

    public List<String> onTabComplete(T sender, String[] args) {
        final AbstractCommandSender<T> abstractSender = abstractSender(sender);
        try {
            return command.autocomplete(abstractSender, args);
        } catch (Exception ex) {
            abstractSender.sendMessage("§c[HoloSports-Commons] An internal error has occurred whilst executing this command.");
            return Collections.emptyList();
        }
    }

    public abstract AbstractCommandSender<T> abstractSender(final T sender);

    public abstract CommandParameters getArguments(final String[] args, final AbstractCommandSender<T> sender);
}