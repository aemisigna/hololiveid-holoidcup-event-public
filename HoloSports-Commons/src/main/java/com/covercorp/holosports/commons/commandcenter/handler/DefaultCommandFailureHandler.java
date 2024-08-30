package com.covercorp.holosports.commons.commandcenter.handler;

import com.covercorp.holosports.commons.commandcenter.CommandNode;
import com.covercorp.holosports.commons.commandcenter.identity.PlayerIdentity;
import com.covercorp.holosports.commons.commandcenter.identity.PlayerIdentityToken;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.shared.util.EnumUtil;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DefaultCommandFailureHandler<T> implements CommandFailureHandler<T> {
    private static CommandFailureHandler<?> instance = new DefaultCommandFailureHandler<>();

    public static <T> CommandFailureHandler<T> getDefault() {
        return (CommandFailureHandler<T>) instance;
    }

    public static void setDefault(final CommandFailureHandler<?> defaultHandler) {
        instance = defaultHandler;
    }

    @Override
    public void onInvalidArgument(final AbstractCommandSender<T> sender, final CommandNode<T> command, final Class<?> required, final String provided) {
        if (Number.class.isAssignableFrom(required)) {
            sender.sendMessage("§c¡El argumento §e" + provided + " §cdebe ser un número!");
        } else if (required.equals(Duration.class)) {
            sender.sendMessage("§cTiempo inválido. Usa el formato: §e7d30h10m");
        } else if (required.isEnum()) {
            final Enum<?>[] values = EnumUtil.getValues((Class<Enum<?>>) required);
            final String available = Stream.of(values).map(Enum::name).collect(Collectors.joining(", "));

            sender.sendMessage("§cArgumento inválido: §e" + provided + "§c, parámetros disponibles: §e" + available);
        } else if (required.equals(PlayerIdentity.class) || required.equals(PlayerIdentityToken.class)) {
            sender.sendMessage("§cEl jugador §e" + provided + " §cno está conectado en la red de servidores.");
        } else if (required.getSimpleName().contains("Player")) {
            sender.sendMessage("§cPlayer §e" + provided + " §cis not connected in this game server. Remember that the HUB server doesn't share players the GAME servers.");
        } else {
            sender.sendMessage("§cEl§e" + provided + " §cno es del tipo " + required.getSimpleName() + "! ¡El comando no puede ser ejecutado");
        }
    }

    @Override
    public void onMissingArgument(AbstractCommandSender<T> sender, CommandNode<T> command, Class<?> required, int index) {
        sender.sendMessage("§cCommand usage: §e" + command.getUsage());
    }

    @Override
    public void onPermissionFail(AbstractCommandSender<T> sender, CommandNode<T> command) {
        sender.sendMessage("§cYou don't have permission to use that command!");
    }

    @Override
    public void onUnsupportedCommandSender(AbstractCommandSender<T> sender, CommandNode<T> command, Class<?> requiredSenderType) {
        sender.sendMessage("§cEste comando solo puede ser ejecutado siendo " + requiredSenderType.getSimpleName() + "!");
    }

    @Override
    public void onCommandFail(AbstractCommandSender<T> sender, CommandNode<T> command, String reason) {
        sender.sendMessage("§c" + reason);
    }
}
