package com.covercorp.holosports.game.tests.soccer.participant;

import com.covercorp.holosports.game.player.BaseParticipant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class SoccerParticipant extends BaseParticipant<ISoccerParticipant> implements ISoccerParticipant {
    private int goals;

    public SoccerParticipant(UUID uniqueId, String name, String displayName) {
        super(uniqueId, name, displayName);

        setParticipant(this);

        goals = 0;
    }
}
