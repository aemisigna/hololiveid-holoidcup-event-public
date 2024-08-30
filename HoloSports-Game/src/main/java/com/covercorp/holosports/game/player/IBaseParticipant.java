package com.covercorp.holosports.game.player;

import java.util.UUID;

public interface IBaseParticipant {
    UUID getUniqueId();
    String getName();
    String getDisplayName();
}
