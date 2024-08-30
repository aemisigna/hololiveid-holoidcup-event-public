package com.covercorp.holosports.game.minigame.bentengan.player.player;

import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class BentenganPlayer implements IBentenganPlayer {
    private final UUID uniqueId;
    private final String name;

    private @Nullable IBentenganTeam team;
}
