package com.covercorp.holosports.game.minigame.bentengan.config;

import com.covercorp.holosports.commons.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public final class BentenganConfigHelper {
    private final ConfigurationSection bentenganSection;
    private final ConfigurationSection teamSection;

    private final static String BENTENGAN_SECTION = "games.bentengan";
    private final static String SPAWN_POINT_SECTION = "spawn-points";
    private final static String LOBBY_SPAWN = "lobby";

    private final static String TEAM_SECTION = BENTENGAN_SECTION + ".teams";
    private final static String TEAM_NAME = "name";
    private final static String TEAM_COLOR = "color";

    public BentenganConfigHelper(final FileConfiguration fileConfiguration) {
        bentenganSection = fileConfiguration.getConfigurationSection(BENTENGAN_SECTION);
        teamSection = fileConfiguration.getConfigurationSection(TEAM_SECTION);
    }

    public Location getLobbySpawn() {
        return bentenganSection.getLocation(SPAWN_POINT_SECTION + "." + LOBBY_SPAWN);
    }

    public Location getTeamChooseNpcLocation() {
        return bentenganSection.getLocation("npcs.team-selector");
    }

    public Location getHubNpcLocation() {
        return bentenganSection.getLocation("npcs.hub");
    }

    public String getTeamDisplay(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_NAME);
    }

    public String getTeamColor(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_COLOR);
    }

    public Location getTeamSpawnPoint(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".spawn-point");
    }

    public Location getTeamJailSpawnPoint(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + ".jail.spawn");
    }

    public Cuboid getMidZone() {
        final Location pos1 = bentenganSection.getLocation("arena-mid.pos1");
        if (pos1 == null) return null;

        final Location pos2 = bentenganSection.getLocation("arena-mid.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getTeamZone(final String teamIdentifier) {
        final Location pos1 = teamSection.getLocation( teamIdentifier + ".zone.pos1");
        if (pos1 == null) return null;

        final Location pos2 = teamSection.getLocation(teamIdentifier + ".zone.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getTeamJailZone(final String teamIdentifier) {
        final Location pos1 = teamSection.getLocation( teamIdentifier + ".jail.pos1");
        if (pos1 == null) return null;

        final Location pos2 = teamSection.getLocation(teamIdentifier + ".jail.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Cuboid getTeamBeaconZone(final String teamIdentifier) {
        final Location pos1 = teamSection.getLocation( teamIdentifier + ".beacon.pos1");
        if (pos1 == null) return null;

        final Location pos2 = teamSection.getLocation(teamIdentifier + ".beacon.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }

    public Set<String> getTeamIdentifiers() {
        return teamSection.getKeys(false);
    }
}
