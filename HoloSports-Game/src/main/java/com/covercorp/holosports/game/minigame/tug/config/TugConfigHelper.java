package com.covercorp.holosports.game.minigame.tug.config;

import com.covercorp.holosports.commons.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public final class TugConfigHelper {
    private final ConfigurationSection tugSection;
    private final ConfigurationSection teamSection;

    private final static String TUG_SECTION = "games.tug";
    private final static String SPAWN_POINT_SECTION = "spawn-points";
    private final static String LOBBY_SPAWN = "lobby";

    private final static String TEAM_SECTION = TUG_SECTION + ".teams";
    private final static String TEAM_NAME = "name";
    private final static String TEAM_COLOR = "color";
    private final static String STANDARD = "standard";

    private final static String ARENA_EDGES = "arena-edges";

    public TugConfigHelper(final FileConfiguration fileConfiguration) {
        tugSection = fileConfiguration.getConfigurationSection(TUG_SECTION);
        teamSection = fileConfiguration.getConfigurationSection(TEAM_SECTION);
    }

    public Location getLobbySpawn() {
        return tugSection.getLocation(SPAWN_POINT_SECTION + "." + LOBBY_SPAWN);
    }

    public Location getTeamChooseNpcLocation() {
        return tugSection.getLocation("npcs.team-selector");
    }

    public Location getHubNpcLocation() {
        return tugSection.getLocation("npcs.hub");
    }

    public String getTeamDisplay(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_NAME);
    }

    public String getTeamColor(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_COLOR);
    }

    public Location getTeamSpawn(final String teamIdentifier) {
        return teamSection.getLocation(teamIdentifier + "." + SPAWN_POINT_SECTION + "." + STANDARD);
    }

    public Location getRopeCenter() {
        return tugSection.getLocation("center");
    }

    public Set<String> getTeamIdentifiers() {
        return teamSection.getKeys(false);
    }

    public Cuboid getArenaCuboid() {
        final Location pos1 = tugSection.getLocation(ARENA_EDGES + ".pos1");
        if (pos1 == null) return null;

        final Location pos2 = tugSection.getLocation(ARENA_EDGES + ".pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }
}
