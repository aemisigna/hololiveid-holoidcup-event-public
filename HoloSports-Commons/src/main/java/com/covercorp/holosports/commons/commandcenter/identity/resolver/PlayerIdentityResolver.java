package com.covercorp.holosports.commons.commandcenter.identity.resolver;

import com.covercorp.holosports.commons.commandcenter.identity.PlayerIdentity;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerIdentityResolver {
    CompletableFuture<Optional<PlayerIdentity>> findByName(String name);

    CompletableFuture<Optional<PlayerIdentity>> findByUuid(UUID uuid);
}
