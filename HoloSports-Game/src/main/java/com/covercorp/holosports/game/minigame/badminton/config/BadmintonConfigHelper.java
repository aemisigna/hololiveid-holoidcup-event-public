package com.covercorp.holosports.game.minigame.badminton.config;

import com.covercorp.holosports.commons.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;

public final class BadmintonConfigHelper {
    private final ConfigurationSection badmintonSection;
    private final ConfigurationSection teamSection;

    private final static String BADMINTON_SECTION = "games.badminton";
    private final static String SPAWN_POINT_SECTION = "spawn-points";
    private final static String LOBBY_SPAWN = "lobby";

    private final static String TEAM_SECTION = BADMINTON_SECTION + ".teams";
    private final static String TEAM_NAME = "name";
    private final static String TEAM_COLOR = "color";
    private final static String STANDARD = "standard";

    private final static String ARENA_EDGES = "arena-edges";

    public BadmintonConfigHelper(final FileConfiguration fileConfiguration) {
        badmintonSection = fileConfiguration.getConfigurationSection(BADMINTON_SECTION);
        teamSection = fileConfiguration.getConfigurationSection(TEAM_SECTION);
    }

    public Location getLobbySpawn() {
        return badmintonSection.getLocation(SPAWN_POINT_SECTION + "." + LOBBY_SPAWN);
    }

    public Location getTeamChooseNpcLocation() {
        return badmintonSection.getLocation("npcs.team-selector");
    }

    public Location getHubNpcLocation() {
        return badmintonSection.getLocation("npcs.hub");
    }

    public String getTeamDisplay(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_NAME);
    }

    public String getTeamColor(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_COLOR);
    }

    // Point zones
    public Cuboid getTeamSinglePointZone(final String teamIdentifier) {
        return new Cuboid(
                teamSection.getLocation(teamIdentifier + ".point-area-singles.normal-point.pos1"),
                teamSection.getLocation(teamIdentifier + ".point-area-singles.normal-point.pos2")
        );
    }

    public Cuboid getTeamDoublePointZone(final String teamIdentifier) {
        return new Cuboid(
                teamSection.getLocation(teamIdentifier + ".point-area-doubles.normal-point.pos1"),
                teamSection.getLocation(teamIdentifier + ".point-area-doubles.normal-point.pos2")
        );
    }

    public Cuboid getTeamSingleServePointZoneEven(final String teamIdentifier) {
        return new Cuboid(
                teamSection.getLocation(teamIdentifier + ".point-area-singles.serve-point.score-even.pos1"),
                teamSection.getLocation(teamIdentifier + ".point-area-singles.serve-point.score-even.pos2")
        );
    }
    public Cuboid getTeamSingleServePointZoneOdd(final String teamIdentifier) {
        return new Cuboid(
                teamSection.getLocation(teamIdentifier + ".point-area-singles.serve-point.score-odd.pos1"),
                teamSection.getLocation(teamIdentifier + ".point-area-singles.serve-point.score-odd.pos2")
        );
    }

    public Cuboid getTeamDoubleServePointZoneEven(final String teamIdentifier) {
        return new Cuboid(
                teamSection.getLocation(teamIdentifier + ".point-area-doubles.serve-point.score-even.pos1"),
                teamSection.getLocation(teamIdentifier + ".point-area-doubles.serve-point.score-even.pos2")
        );
    }
    public Cuboid getTeamDoubleServePointZoneOdd(final String teamIdentifier) {
        return new Cuboid(
                teamSection.getLocation(teamIdentifier + ".point-area-doubles.serve-point.score-odd.pos1"),
                teamSection.getLocation(teamIdentifier + ".point-area-doubles.serve-point.score-odd.pos2")
        );
    }

    public Location getSingleServePositionEven(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".serve-positions-singles.serve-even");
    }

    public Location getSingleServePositionOdd(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".serve-positions-singles.serve-odd");
    }

    public Location getDoubleServePositionEven(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".serve-positions-doubles.serve-even");
    }

    public Location getDoubleServePositionOdd(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".serve-positions-doubles.serve-odd");
    }

    public Location getShuttlecockSpawnEven(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".shuttlecock-spawn-points.score-even");
    }

    public Location getShuttlecockSpawnOdd(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".shuttlecock-spawn-points.score-odd");
    }

    public Location getShuttlecockSpawnNormal(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".shuttlecock-spawn-points.normal-play");
    }

    public List<Location> getTeamStandardSpawns(final String teamIdentifier) {
        return (List<Location>) teamSection.getList(teamIdentifier + "." + SPAWN_POINT_SECTION + "." + STANDARD);
    }

    public Set<String> getTeamIdentifiers() {
        return teamSection.getKeys(false);
    }

    public Cuboid getArenaCuboid() {
        final Location pos1 = badmintonSection.getLocation(ARENA_EDGES + ".pos1");
        if (pos1 == null) return null;

        final Location pos2 = badmintonSection.getLocation(ARENA_EDGES + ".pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }
}
