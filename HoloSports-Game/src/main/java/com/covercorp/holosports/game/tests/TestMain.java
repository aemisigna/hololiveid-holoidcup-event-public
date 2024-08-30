package com.covercorp.holosports.game.tests;

import com.covercorp.holosports.game.manager.BaseParticipantStorage;
import com.covercorp.holosports.game.tests.soccer.SoccerParticipantStorage;
import com.covercorp.holosports.game.tests.soccer.participant.ISoccerParticipant;
import com.covercorp.holosports.game.tests.soccer.participant.SoccerParticipant;

import java.util.UUID;

public class TestMain {
    private final BaseParticipantStorage<ISoccerParticipant> soccerParticipantBaseParticipantStorage;

    public TestMain() {
        soccerParticipantBaseParticipantStorage = new SoccerParticipantStorage();

        final ISoccerParticipant soccerParticipant = new SoccerParticipant(
                UUID.randomUUID(),
                "Test",
                "Test"
        );

        soccerParticipantBaseParticipantStorage.register(soccerParticipant);
    }

    public static void main(String[] args) {
        new TestMain();
    }
}
