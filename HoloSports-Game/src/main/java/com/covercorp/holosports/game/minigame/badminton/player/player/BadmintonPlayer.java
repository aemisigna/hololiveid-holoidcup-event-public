package com.covercorp.holosports.game.minigame.badminton.player.player;

import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class BadmintonPlayer implements IBadmintonPlayer {
    private final UUID uniqueId;
    private final String name;

    private @Nullable IBadmintonTeam team;
}
