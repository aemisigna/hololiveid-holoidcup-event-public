package com.covercorp.holosports.commons.commandcenter;

import com.covercorp.holosports.commons.commandcenter.exception.InvalidArgumentException;
import com.covercorp.holosports.commons.commandcenter.handler.CommandFailureHandler;
import com.covercorp.holosports.commons.commandcenter.identity.PlayerIdentity;
import com.covercorp.holosports.commons.commandcenter.identity.PlayerIdentityToken;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.commons.commandcenter.exception.MissingArgumentException;
import com.covercorp.holosports.shared.util.EnumUtil;

import java.util.Arrays;
import java.util.UUID;

public class CommandParameters {
    private CommandNode<?> command;
    private AbstractCommandSender<?> sender;
    private String[] args;
    private int offset;

    protected CommandParameters(final CommandNode<?> command, final AbstractCommandSender<?> sender, final String[] args) {
        this.command = command;
        this.sender = sender;
        this.args = args;
        this.offset = 0;
    }

    protected CommandParameters next(CommandNode<?> nextNode, int next) {
        this.offset += next;
        this.command = nextNode;
        return this;
    }

    public boolean isPresent(int index) {
        int actualIndex = index + offset;

        if (actualIndex < offset) actualIndex--;

        return actualIndex >= 0 && actualIndex < args.length;
    }

    protected int translateIndex(int index, Class<?> indexType) throws MissingArgumentException {
        int actualIndex = index + offset;

        if (index < 0) actualIndex--;
        if (args.length <= actualIndex) throw new MissingArgumentException(command, indexType, index);
        if (actualIndex < 0) {
            final int min = -offset + 1;
            final int max = args.length - offset - 1;

            throw new IndexOutOfBoundsException("Index " + index + " out of bounds [" + min + ", " + max + "]");
        }

        return actualIndex;
    }

    protected CommandNode<?> getCommand() {
        return command;
    }

    public String get(int index) throws MissingArgumentException {
        int actualIndex = translateIndex(index, String.class);
        return args[actualIndex];
    }

    public String getOrElse(int index, String defaultVal) {
        try {
            return isPresent(index) ? get(index) : defaultVal;
        } catch (MissingArgumentException e) {
            throw new RuntimeException("si ves esto, es que algo salió tan mal que hay que formatear el sistema.", e);
        }
    }

    public int getInt(int index) throws InvalidArgumentException {
        int actualIndex = translateIndex(index, Integer.class);
        String arg = args[actualIndex];
        try {
            int multiplier = 1;
            if (arg.endsWith("k")) {
                multiplier = 1_000;
                arg = arg.substring(0, arg.length() - 1);
            } else if (arg.endsWith("m")) {
                multiplier = 1_000_000;
                arg = arg.substring(0, arg.length() - 1);
            }
            return Integer.parseInt(arg) * multiplier;
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(command, Integer.class, arg);
        }
    }

    public double getDouble(int index) throws InvalidArgumentException {
        int actualIndex = translateIndex(index, Double.class);
        String arg = args[actualIndex].replace(",", ".");
        try {
            int multiplier = 1;
            if (arg.endsWith("k")) {
                multiplier = 1_000;
                arg = arg.substring(0, arg.length() - 1);
            } else if (arg.endsWith("m")) {
                multiplier = 1_000_000;
                arg = arg.substring(0, arg.length() - 1);
            }
            return Double.parseDouble(arg) * multiplier;
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(command, Double.class, arg);
        }
    }

    public boolean getBoolean(int index) throws InvalidArgumentException {
        int actualIndex = translateIndex(index, Integer.class);
        String arg = args[actualIndex];

        final String[] trueValues = { "true", "yes", "on", "enable", "enabled", "1" };
        for (String trueStr : trueValues) {
            if (trueStr.equalsIgnoreCase(arg)) {
                return true;
            }
        }

        final String[] falseValues = { "false", "no", "off", "disable", "disabled", "0" };
        for (String falseStr : falseValues) {
            if (falseStr.equalsIgnoreCase(arg)) {
                return false;
            }
        }

        throw new InvalidArgumentException(command, Boolean.class, arg);
    }

    public <T extends Enum<?>> T getEnum(final int index, final Class<T> enumType) throws InvalidArgumentException {
        final int actualIndex = translateIndex(index, enumType);
        final String name = args[actualIndex];
        final T[] values = EnumUtil.getValues(enumType);

        for (T t : values) {
            if (t.name().equalsIgnoreCase(name)) return t;
        }

        throw new InvalidArgumentException(this.command, enumType, name);
    }

    /*
    public Duration getDuration(int index) throws InvalidArgumentException {
        final int actualIndex = translateIndex(index, Double.class);
        final String arg = args[actualIndex];

        try {
            return DurationParser.parseDuration(arg);
        } catch (IllegalArgumentException ex) {
            throw new InvalidArgumentException(this.command, Duration.class, arg);
        }
    }*/

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PlayerIdentityToken getIdentity(int index) throws InvalidArgumentException {
        final int actualIndex = translateIndex(index, PlayerIdentity.class);
        final String arg = args[actualIndex];
        PlayerIdentityToken token;
        try {
            token = PlayerIdentityToken.ofUuid(UUID.fromString(arg));
        } catch (IllegalArgumentException ex) {
            token = PlayerIdentityToken.ofName(arg);
        }
        token.whenUnknown(unknown -> getFailHandler().onInvalidArgument((AbstractCommandSender) sender, (CommandNode) command, PlayerIdentity.class, arg));
        return token;
    }

    /*
    @SuppressWarnings("rawtypes")
    public OfflinePlayer getIdentity(int index) throws InvalidArgumentException {
        final int actualIndex = translateIndex(index, OfflinePlayer.class);
        final String arg = args[actualIndex];

        OfflinePlayer offlinePlayer;

        try {
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(arg));
        } catch (IllegalArgumentException ex) {
            offlinePlayer = Bukkit.getOfflinePlayer(arg);
        }


        if (!offlinePlayer.hasPlayedBefore()) getFailHandler().onInvalidArgument((AbstractCommandSender) sender, (CommandNode) command, OfflinePlayer.class, arg);

        return offlinePlayer;
    }*/

    private CommandFailureHandler<?> getFailHandler() {
        if (command instanceof CommandRoot<?>) {
            return ((CommandRoot<?>) command).getFailHandler();
        } else if (command instanceof CommandChild<?>) {
            return ((CommandChild<?>) command).getRoot().getFailHandler();
        } else {
            throw new IllegalStateException();
        }
    }

    public String join(final String delimiter) throws InvalidArgumentException {
        return join(delimiter, 0);
    }

    public String join(final String delimiter, final int start) throws InvalidArgumentException {
        int actualIndex = translateIndex(start, String.class);

        return String.join(delimiter, Arrays.copyOfRange(args, actualIndex, args.length));
    }

    public String join(final int start) throws InvalidArgumentException {
        return this.join(" ", start);
    }

    public String join() throws InvalidArgumentException {
        return this.join(" ");
    }

    public boolean checkOptional(final int index, final String str) {
        if (!isPresent(index)) {
            return false;
        }

        try {
            return get(index).equalsIgnoreCase(str);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException("command index out of bounds: " + index);
        }
    }

    public boolean checkWithCaseOptional(final int index, final String str) {
        if (!isPresent(index)) return false;

        try {
            return get(index).equals(str);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException("command index out of bounds: " + index);
        }
    }

    public boolean check(final int index, final String str) throws MissingArgumentException {
        return get(index).equalsIgnoreCase(str);
    }

    public boolean checkWithCase(final int index, final String str) throws MissingArgumentException {
        return get(index).equals(str);
    }

    public String[] toArray() {
        return Arrays.copyOfRange(args, offset, args.length);
    }

    public int length() {
        return this.args.length - offset;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public String toString() {
        return String.join(" ", args);
    }
}
