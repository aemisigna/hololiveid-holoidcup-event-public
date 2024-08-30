package com.covercorp.holosports.commons.commandcenter.identity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

public interface PlayerIdentity {
    static PlayerIdentity snapshot(final UUID uuid, final String name, final boolean online) {
        return new PlayerIdentitySnapshot(uuid, name, true);
    }

    static PlayerIdentity offline(UUID uuid, String name) {
        return new PlayerIdentitySnapshot(uuid, name, false);
    }

    UUID getUuid();

    String getName();

    boolean isOnline();

    @Getter(AccessLevel.PUBLIC)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = "uuid")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class PlayerIdentitySnapshot implements PlayerIdentity {
        UUID uuid;
        String name;
        boolean online;
    }
}