package com.covercorp.holosports.commons.commandcenter.handler;

import com.covercorp.holosports.commons.commandcenter.CommandNode;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

public interface CommandFailureHandler<T> {
    void onInvalidArgument(AbstractCommandSender<T> sender, CommandNode<T> command, Class<?> required, String provided);

    void onMissingArgument(AbstractCommandSender<T> sender, CommandNode<T> command, Class<?> required, int index);

    void onPermissionFail(AbstractCommandSender<T> sender, CommandNode<T> command);

    void onUnsupportedCommandSender(AbstractCommandSender<T> sender, CommandNode<T> command, Class<?> requiredSenderType);

    void onCommandFail(AbstractCommandSender<T> sender, CommandNode<T> command, String reason);
}
