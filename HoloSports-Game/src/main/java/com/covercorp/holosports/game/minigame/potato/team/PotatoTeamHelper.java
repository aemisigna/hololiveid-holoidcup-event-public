package com.covercorp.holosports.game.minigame.potato.team;

import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import com.covercorp.holosports.game.minigame.potato.team.team.PotatoTeam;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.config.PotatoConfigHelper;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PotatoTeamHelper implements IPotatoTeamHelper {
    private final PotatoMiniGame potatoMiniGame;

    private final Map<String, IPotatoTeam> teams;

    public PotatoTeamHelper(final PotatoMiniGame potatoMiniGame) {
        this.potatoMiniGame = potatoMiniGame;

        teams = new HashMap<>();
    }

    @Override
    public boolean registerTeam(final String identifier, final String display, final String color) {
        try {
            final IPotatoTeam potatoTeam = new PotatoTeam(identifier, display, color);

            teams.put(identifier, potatoTeam);

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void registerTeams() {
        final PotatoConfigHelper potatoConfigHelper = potatoMiniGame.getPotatoConfigHelper();

        potatoConfigHelper.getTeamIdentifiers().forEach(teamId -> {
            final boolean res = registerTeam(
                    teamId,
                    potatoConfigHelper.getTeamDisplay(teamId),
                    potatoConfigHelper.getTeamColor(teamId)
            );

            if (!res) {
                potatoMiniGame.getHoloSportsGame().getLogger().severe("Could not register team: " + teamId);
                return;
            }

            potatoMiniGame.getHoloSportsGame().getLogger().info("Registered Team: " + teamId);
        });
    }

    @Override
    public boolean unregisterTeam(final String teamIdentifier) {
        final Optional<IPotatoTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IPotatoTeam team = teamOptional.get();

        teams.remove(team.getIdentifier());

        return true;
    }

    @Override
    public void unregisterTeams() {
        getTeamList().forEach(team -> {
            final boolean res = unregisterTeam(team.getIdentifier());

            if (!res) {
                potatoMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister team: " + team.getIdentifier());
                return;
            }

            potatoMiniGame.getHoloSportsGame().getLogger().info("Unregistered team: " + team.getIdentifier());
        });
    }

    @Override
    public boolean addPlayerToTeam(final IPotatoPlayer player, final String teamIdentifier) {
        if (player.getTeam() != null) removePlayerFromTeam(player, player.getTeam().getIdentifier());

        final Optional<IPotatoTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IPotatoTeam team = teamOptional.get();

        team.addPlayer(player);
        player.setTeam(team);

        potatoMiniGame.getHoloSportsGame().getLogger().info("Added player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public boolean removePlayerFromTeam(final IPotatoPlayer player, final String teamIdentifier) {
        final Optional<IPotatoTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IPotatoTeam team = teamOptional.get();

        team.removePlayer(player);
        player.setTeam(null);

        potatoMiniGame.getHoloSportsGame().getLogger().info("Removed player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public Optional<IPotatoTeam> getTeam(final String teamIdentifier) {
        return Optional.ofNullable(teams.get(teamIdentifier));
    }

    @Override
    public ImmutableList<IPotatoTeam> getTeamList() {
        return ImmutableList.copyOf(teams.values());
    }

    @Override
    public IPotatoTeam getOppositeTeam(IPotatoTeam team) {
        return getTeamList().stream().filter(t -> !t.getIdentifier().equals(team.getIdentifier())).findFirst().orElse(null);
    }
}
