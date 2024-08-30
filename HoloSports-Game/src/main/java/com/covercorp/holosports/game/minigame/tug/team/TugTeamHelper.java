package com.covercorp.holosports.game.minigame.tug.team;

import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.config.TugConfigHelper;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.team.TugTeam;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TugTeamHelper implements ITugTeamHelper {
    private final TugMiniGame tugMiniGame;

    private final Map<String, ITugTeam> teams;

    public TugTeamHelper(final TugMiniGame tugMiniGame) {
        this.tugMiniGame = tugMiniGame;

        teams = new HashMap<>();
    }

    @Override
    public boolean registerTeam(final String identifier, final String display, final String color, final Location spawn) {
        try {
            final ITugTeam tugTeam = new TugTeam(identifier, display, color, spawn);

            teams.put(identifier, tugTeam);

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void registerTeams() {
        final TugConfigHelper tugConfigHelper = tugMiniGame.getTugConfigHelper();

        tugConfigHelper.getTeamIdentifiers().forEach(teamId -> {
            final boolean res = registerTeam(
                    teamId,
                    tugConfigHelper.getTeamDisplay(teamId),
                    tugConfigHelper.getTeamColor(teamId),
                    tugConfigHelper.getTeamSpawn(teamId)
            );

            if (!res) {
                tugMiniGame.getHoloSportsGame().getLogger().severe("Could not register team: " + teamId);
                return;
            }

            tugMiniGame.getHoloSportsGame().getLogger().info("Registered Team: " + teamId);
        });
    }

    @Override
    public boolean unregisterTeam(final String teamIdentifier) {
        final Optional<ITugTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final ITugTeam team = teamOptional.get();

        teams.remove(team.getIdentifier());

        return true;
    }

    @Override
    public void unregisterTeams() {
        getTeamList().forEach(team -> {
            final boolean res = unregisterTeam(team.getIdentifier());

            if (!res) {
                tugMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister team: " + team.getIdentifier());
                return;
            }

            tugMiniGame.getHoloSportsGame().getLogger().info("Unregistered team: " + team.getIdentifier());
        });
    }

    @Override
    public boolean addPlayerToTeam(final ITugPlayer player, final String teamIdentifier) {
        if (player.getTeam() != null) removePlayerFromTeam(player, player.getTeam().getIdentifier());

        final Optional<ITugTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final ITugTeam team = teamOptional.get();

        team.addPlayer(player);
        player.setTeam(team);

        tugMiniGame.getHoloSportsGame().getLogger().info("Added player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public boolean removePlayerFromTeam(final ITugPlayer player, final String teamIdentifier) {
        final Optional<ITugTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final ITugTeam team = teamOptional.get();

        team.removePlayer(player);
        player.setTeam(null);

        tugMiniGame.getHoloSportsGame().getLogger().info("Removed player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public Optional<ITugTeam> getTeam(final String teamIdentifier) {
        return Optional.ofNullable(teams.get(teamIdentifier));
    }

    @Override
    public ImmutableList<ITugTeam> getTeamList() {
        return ImmutableList.copyOf(teams.values());
    }

    @Override
    public Optional<ITugTeam> getTeamWithMostPoints() {
        // Get the first two teams of the team list
        final ITugTeam firstTeam = getTeamList().get(0);
        final ITugTeam secondTeam = getTeamList().get(1);

        // Compare both teams using goals, return the winner one. If tie, return empty optional
        if (firstTeam.getPoints() > secondTeam.getPoints()) return Optional.of(firstTeam);
        else if (firstTeam.getPoints() < secondTeam.getPoints()) return Optional.of(secondTeam);
        else return Optional.empty();
    }

    @Override
    public ITugTeam getOppositeTeam(ITugTeam team) {
        return getTeamList().stream().filter(t -> !t.getIdentifier().equals(team.getIdentifier())).findFirst().orElse(null);
    }
}
