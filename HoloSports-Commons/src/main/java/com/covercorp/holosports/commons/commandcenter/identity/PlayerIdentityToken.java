package com.covercorp.holosports.commons.commandcenter.identity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import com.covercorp.holosports.commons.commandcenter.CommandCenter;
import com.covercorp.holosports.commons.commandcenter.identity.resolver.PlayerIdentityResolver;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerIdentityToken {
    private Consumer<PlayerIdentity> successConsumer;
    private Consumer<PlayerIdentityToken> failConsumer;

    private final String name;
    private final UUID uuid;

    private PlayerIdentityResolver resolver = CommandCenter.getIdentityResolver();
    private volatile boolean resolving;

    public static PlayerIdentityToken ofName(final String name) {
        return new PlayerIdentityToken(name, null);
    }

    public static PlayerIdentityToken ofUuid(final UUID uuid) {
        return new PlayerIdentityToken(null, uuid);
    }

    protected void fail() {
        failConsumer.accept(this);
    }

    protected void success(PlayerIdentity identity) {
        successConsumer.accept(identity);
    }

    public Object getIdentifier() {
        return isIdentifiedByUuid() ? uuid : name;
    }

    public boolean isIdentifiedByName() {
        return name != null;
    }

    public boolean isIdentifiedByUuid() {
        return uuid != null;
    }

    public PlayerIdentityToken whenFound(final Consumer<PlayerIdentity> identityConsumer) {
        if (successConsumer != null) throw new IllegalStateException("whenFound action already set.");

        successConsumer = identityConsumer;

        resolve();

        return this;
    }

    public <T> PlayerIdentityToken whenFound(final BiConsumer<PlayerIdentity, T> function, T param) {
        return whenFound(identity -> function.accept(identity, param));
    }

    public PlayerIdentityToken whenUnknown(final Consumer<PlayerIdentityToken> unknownTokenConsumer) {
        failConsumer = unknownTokenConsumer;

        return this;
    }

    public PlayerIdentityToken withResolver(final PlayerIdentityResolver resolver) {
        this.resolver = resolver;

        return this;
    }

    @Synchronized
    private PlayerIdentityToken resolve() {
        if (resolving) throw new IllegalStateException("Token already resolved.");

        resolving = true;

        if (isIdentifiedByName()) {
            resolver.findByName(name)
                    .thenAccept(idOptional -> {
                        success(idOptional.get());
                    })
                    .exceptionally(ex -> {
                        fail();
                        return null;
                    });
        } else if (isIdentifiedByUuid()) {
            resolver.findByUuid(uuid)
                    .thenAccept(idOptional -> {
                        success(idOptional.get());
                    })
                    .exceptionally(ex -> {
                        fail();
                        return null;
                    });
        } else {
            throw new IllegalStateException();
        }

        return this;
    }
}