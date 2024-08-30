package com.covercorp.holosports.game.manager;

import com.covercorp.holosports.game.player.IBaseParticipant;

import com.google.common.collect.ImmutableList;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseParticipantStorage<T extends IBaseParticipant> {
    @Getter(AccessLevel.PROTECTED) private final Map<UUID, T> participantMap;

    public BaseParticipantStorage(final Map<UUID, T> mapType) {
        this.participantMap = mapType;
    }

    public abstract T register(final T participant);
    public abstract T unregister(final UUID uuid);

    public abstract Optional<T> get(final UUID uuid);

    public ImmutableList<T> getAll() {
        return ImmutableList.copyOf(participantMap.values());
    }
}
