package com.covercorp.holosports.game.tests.soccer;

import com.covercorp.holosports.game.manager.BaseParticipantStorage;
import com.covercorp.holosports.game.tests.soccer.participant.ISoccerParticipant;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SoccerParticipantStorage extends BaseParticipantStorage<ISoccerParticipant> {
    public SoccerParticipantStorage() {
        super(new ConcurrentHashMap<>());
    }

    @Override
    public ISoccerParticipant register(final ISoccerParticipant participant) {
        return getParticipantMap().put(participant.getUniqueId(), participant);
    }

    @Override
    public ISoccerParticipant unregister(final UUID uniqueId) {
        return getParticipantMap().remove(uniqueId);
    }

    @Override
    public Optional<ISoccerParticipant> get(final UUID uniqueId) {
        return Optional.ofNullable(getParticipantMap().get(uniqueId));
    }
}
