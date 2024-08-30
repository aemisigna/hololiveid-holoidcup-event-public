package com.covercorp.holosports.game.minigame.potato.team;

import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.Optional;

public interface IPotatoTeamHelper {
    boolean registerTeam(final String identifier,
                         final String display,
                         final String color);
    void registerTeams();
    boolean unregisterTeam(final String teamIdentifier);
    void unregisterTeams();

    boolean addPlayerToTeam(final IPotatoPlayer player, String teamIdentifier);
    boolean removePlayerFromTeam(final IPotatoPlayer player, String teamIdentifier);

    Optional<IPotatoTeam> getTeam(final String teamIdentifier);

    ImmutableList<IPotatoTeam> getTeamList();

    IPotatoTeam getOppositeTeam(final IPotatoTeam team);
}
