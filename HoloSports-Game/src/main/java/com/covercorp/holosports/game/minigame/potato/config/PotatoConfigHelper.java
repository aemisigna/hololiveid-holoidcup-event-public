package com.covercorp.holosports.game.minigame.potato.config;

import com.covercorp.holosports.commons.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public final class PotatoConfigHelper {
    private final ConfigurationSection potatoSection;
    private final ConfigurationSection teamSection;

    private final static String POTATO_SECTION = "games.potato";
    private final static String SPAWN_POINT_SECTION = "spawn-points";
    private final static String LOBBY_SPAWN = "lobby";

    private final static String TEAM_SECTION = POTATO_SECTION + ".teams";
    private final static String TEAM_NAME = "name";
    private final static String TEAM_COLOR = "color";
    private final static String STANDARD = "standard";

    private final static String ARENA_EDGES = "arena-edges";

    public PotatoConfigHelper(final FileConfiguration fileConfiguration) {
        potatoSection = fileConfiguration.getConfigurationSection(POTATO_SECTION);
        teamSection = fileConfiguration.getConfigurationSection(TEAM_SECTION);
    }

    public Location getLobbySpawn() {
        return potatoSection.getLocation(SPAWN_POINT_SECTION + "." + LOBBY_SPAWN);
    }

    public Location getTeamChooseNpcLocation() {
        return potatoSection.getLocation("npcs.team-selector");
    }

    public Location getHubNpcLocation() {
        return potatoSection.getLocation("npcs.hub");
    }

    public String getTeamDisplay(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_NAME);
    }

    public String getTeamColor(final String teamIdentifier) {
        return teamSection.getString(teamIdentifier + "." + TEAM_COLOR);
    }

    public Set<String> getTeamIdentifiers() {
        return teamSection.getKeys(false);
    }

    public Location getGoalLocation(final String goalIdentifier) {
        return potatoSection.getLocation("goals." + goalIdentifier + ".center");
    }

    public Cuboid getGoalCuboid(final String goalIdentifier) {
        final Location pos1 = potatoSection.getLocation("goals." + goalIdentifier + ".cuboid.pos1");
        if (pos1 == null) return null;

        final Location pos2 = potatoSection.getLocation("goals." + goalIdentifier + ".cuboid.pos2");
        if (pos2 == null) return null;

        return new Cuboid(pos1, pos2);
    }
}
