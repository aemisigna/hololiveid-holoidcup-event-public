package com.covercorp.holosports.game.minigame.soccer.player.role;

public enum SoccerRole {
    GOALKEEPER("GOALKEEPER"),
    STANDARD("STANDARD"),
    REFEREE("REFEREE"),
    VIEWER("VIEWER");

    private final String identifier;

    SoccerRole(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
