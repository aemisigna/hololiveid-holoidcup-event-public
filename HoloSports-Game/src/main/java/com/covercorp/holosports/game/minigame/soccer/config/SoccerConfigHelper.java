package com.covercorp.holosports.game.minigame.soccer.config;

import com.covercorp.holosports.commons.util.Cuboid;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;

public final class SoccerConfigHelper {
    private final ConfigurationSection soccerSection;
    private final ConfigurationSection teamSection;

    private final static String SOCCER_SECTION = "games.soccer";
    private final static String SPAWN_POINT_SECTION = "spawn-points";
    private final static String REFEREE_SPAWN = "referee";
    private final static String LOBBY_SPAWN = "lobby";

    private final static String TEAM_SECTION = SOCCER_SECTION + ".teams";
    private final static String TEAM_NAME = "name";
    private final static String TEAM_COLOR = "color";
    private final static String GOALKEEPER = "goalkeeper";
    private final static String STANDARD = "standard";

    private final static String ARENA_EDGES = "arena-edges";

    public SoccerConfigHelper(final FileConfiguration fileConfiguration) {
        soccerSection = fileConfiguration.getConfigurationSection(SOCCER_SECTION);
        teamSection = fileConfiguration.getConfigurationSection(TEAM_SECTION);
    }

    public Location getRefereeSpawn() {
        return soccerSection.getLocation(SPAWN_POINT_SECTION + "." + REFEREE_SPAWN);
    }

    public Location getLobbySpawn() {
        return soccerSection.getLocation(SPAWN_POINT_SECTION + "." + LOBBY_SPAWN);
    }

    public Location getTeamChooseNpcLocation() {
        return soccerSection.getLocation("npcs.team-selector");
    }

    public Location getRoleChooseNpcLocation() {
        return soccerSection.getLocation("npcs.role-selector");
    }

    public Location getHubNpcLocation() {
        return soccerSection.getLocation("npcs.hub");
    }

    public String getTeamDisplay(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_NAME);
    }

    public String getTeamColor(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_COLOR);
    }

    public Location getTeamGoalkeeperSpawn(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + "." + SPAWN_POINT_SECTION + "." + GOALKEEPER);
    }

    public List<Location> getTeamStandardSpawns(final String teamIdentifier) {
        return (List<Location>) teamSection.getList(teamIdentifier + "." + SPAWN_POINT_SECTION + "." + STANDARD);
    }

    public Set<String> getTeamIdentifiers() {
        return teamSection.getKeys(false);
    }

    public Location getStartBallSpawn() {
        return soccerSection.getLocation(SPAWN_POINT_SECTION + ".ball");
    }

    public Cuboid getArenaCuboid() {
        final Location pos1 = soccerSection.getLocation(ARENA_EDGES + ".pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation(ARENA_EDGES + ".pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getTeamGoalCuboid(final String teamIdentifier) {
        final Location pos1 = teamSection.getLocation(teamIdentifier + "." + "goal.pos1");
        if (pos1 == null) return null;

        final Location pos2 = teamSection.getLocation(teamIdentifier + "." + "goal.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getTeamGoalSafeCuboid(final String teamIdentifier) {
        final Location pos1 = teamSection.getLocation(teamIdentifier + "." + "goalkeeper-zone.pos1");
        if (pos1 == null) return null;

        final Location pos2 = teamSection.getLocation(teamIdentifier + "." + "goalkeeper-zone.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getLeftSideCuboid() {
        final Location pos1 = soccerSection.getLocation("arena-sideline-1.pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation("arena-sideline-1.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getRightSideCuboid() {
        final Location pos1 = soccerSection.getLocation("arena-sideline-2.pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation("arena-sideline-2.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getFirstTeamEndCuboid() {
        final Location pos1 = soccerSection.getLocation("arena-endline-1.pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation("arena-endline-1.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getSecondTeamEndCuboid() {
        final Location pos1 = soccerSection.getLocation("arena-endline-2.pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation("arena-endline-2.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    // Penalty mode
    public Cuboid getPenaltyPlayZone() {
        final Location pos1 = soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.play-zone.pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.play-zone.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getPenaltyGoalZone() {
        final Location pos1 = soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.goal-zone.pos1");
        if (pos1 == null) return null;

        final Location pos2 = soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.goal-zone.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Location getPenaltyBallSpawn() {
        return soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.ball-spawn");
    }

    public Location getPenaltyShooterSpawn() {
        return soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.shooter-spawn");
    }

    public Location getPenaltyGoalkeeperSpawn() {
        return soccerSection.getLocation(SPAWN_POINT_SECTION + ".penalty.goalkeeper-spawn");
    }
}
