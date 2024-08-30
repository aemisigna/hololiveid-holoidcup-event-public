package com.covercorp.holosports.commons.commandcenter.identity.resolver;

import com.covercorp.holosports.commons.commandcenter.identity.PlayerIdentity;
import com.covercorp.holosports.shared.HoloSportsShared;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class MinecraftIdentityResolver implements PlayerIdentityResolver {
    @Override
    public CompletableFuture<Optional<PlayerIdentity>> findByName(final String name) {
        final CompletableFuture<Optional<PlayerIdentity>> completableFuture = new CompletableFuture<>();

        HoloSportsShared.getCoreShared().getExecutorProvider().getExecutorService().submit(() -> {
            Optional<PlayerIdentity> playerOptional;

            final Player player = Bukkit.getPlayerExact(name);

            if (player == null) {
                playerOptional = Optional.of(PlayerIdentity.offline(UUID.fromString(name), name));
            } else {
                playerOptional = Optional.of(PlayerIdentity.snapshot(player.getUniqueId(), player.getName(), true));
            }

            completableFuture.complete(playerOptional);
        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<Optional<PlayerIdentity>> findByUuid(final UUID uuid) {
        final CompletableFuture<Optional<PlayerIdentity>> completableFuture = new CompletableFuture<>();

        HoloSportsShared.getCoreShared().getExecutorProvider().getExecutorService().submit(() -> {
            Optional<PlayerIdentity> playerOptional;

            final Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                playerOptional = Optional.of(PlayerIdentity.offline(uuid, uuid.toString()));
            } else {
                playerOptional = Optional.of(PlayerIdentity.snapshot(player.getUniqueId(), player.getName(), true));
            }

            completableFuture.complete(playerOptional);
        });

        return completableFuture;
    }
}