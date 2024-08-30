package com.covercorp.holosports.game.tests.soccer.participant;

import com.covercorp.holosports.game.player.IBaseParticipant;

public interface ISoccerParticipant extends IBaseParticipant {
    int getGoals();
    void setGoals(final int goals);
}
