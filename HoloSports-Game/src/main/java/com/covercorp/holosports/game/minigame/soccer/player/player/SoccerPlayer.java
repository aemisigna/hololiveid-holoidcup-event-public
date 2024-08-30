package com.covercorp.holosports.game.minigame.soccer.player.player;

import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class SoccerPlayer implements ISoccerPlayer {
    private final UUID uniqueId;
    private final String name;

    private @Nullable ISoccerTeam team;

    private @Nullable SoccerRole role;

    @Override
    public boolean isReferee() {
        return role == SoccerRole.REFEREE;
    }
}
