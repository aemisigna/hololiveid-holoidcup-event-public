package com.covercorp.holosports.game.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public abstract class BaseParticipant<T extends IBaseParticipant> {
    private final UUID uniqueId;
    private final String name;
    private final String displayName;

    private T participant;
}
