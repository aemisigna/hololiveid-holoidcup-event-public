package com.covercorp.holosports.game.minigame.soccer.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.config.SoccerConfigHelper;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import com.covercorp.holosports.game.minigame.soccer.team.team.SoccerTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SoccerTeamHelper implements ISoccerTeamHelper {
    private final SoccerMiniGame soccerMiniGame;

    private final Map<String, ISoccerTeam> teams;

    public SoccerTeamHelper(final SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        teams = new HashMap<>();
    }

    @Override
    public boolean registerTeam(final String identifier, final String display, final String color, final Location goalKeeperSpawn, final List<Location> standardSpawns, final Cuboid goalCuboid, final Cuboid goalSafeCuboid) {
        try {
            final ISoccerTeam soccerTeam = new SoccerTeam(identifier, display, color, goalKeeperSpawn, standardSpawns, goalCuboid, goalSafeCuboid);

            teams.put(identifier, soccerTeam);

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void registerTeams() {
        final SoccerConfigHelper soccerConfigHelper = soccerMiniGame.getSoccerConfigHelper();

        soccerConfigHelper.getTeamIdentifiers().forEach(teamId -> {
            final boolean res = registerTeam(
                    teamId,
                    soccerConfigHelper.getTeamDisplay(teamId),
                    soccerConfigHelper.getTeamColor(teamId),
                    soccerConfigHelper.getTeamGoalkeeperSpawn(teamId),
                    soccerConfigHelper.getTeamStandardSpawns(teamId),
                    soccerConfigHelper.getTeamGoalCuboid(teamId),
                    soccerConfigHelper.getTeamGoalSafeCuboid(teamId)
            );

            if (!res) {
                soccerMiniGame.getHoloSportsGame().getLogger().severe("Could not register team: " + teamId);
                return;
            }

            soccerMiniGame.getHoloSportsGame().getLogger().info("Registered Team: " + teamId);
        });
    }

    @Override
    public boolean unregisterTeam(final String teamIdentifier) {
        final Optional<ISoccerTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final ISoccerTeam team = teamOptional.get();

        teams.remove(team.getIdentifier());

        return true;
    }

    @Override
    public void unregisterTeams() {
        getTeamList().forEach(team -> {
            final boolean res = unregisterTeam(team.getIdentifier());

            if (!res) {
                soccerMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister team: " + team.getIdentifier());
                return;
            }

            soccerMiniGame.getHoloSportsGame().getLogger().info("Unregistered team: " + team.getIdentifier());
        });
    }

    @Override
    public boolean addPlayerToTeam(final ISoccerPlayer player, final String teamIdentifier) {
        if (player.getTeam() != null) removePlayerFromTeam(player, player.getTeam().getIdentifier());

        final Optional<ISoccerTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final ISoccerTeam team = teamOptional.get();

        team.addPlayer(player);
        player.setTeam(team);

        soccerMiniGame.getHoloSportsGame().getLogger().info("Added player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public boolean removePlayerFromTeam(final ISoccerPlayer player, final String teamIdentifier) {
        final Optional<ISoccerTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final ISoccerTeam team = teamOptional.get();

        team.removePlayer(player);
        player.setTeam(null);

        soccerMiniGame.getHoloSportsGame().getLogger().info("Removed player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public Optional<ISoccerTeam> getTeam(final String teamIdentifier) {
        return Optional.ofNullable(teams.get(teamIdentifier));
    }

    @Override
    public ImmutableList<ISoccerTeam> getTeamList() {
        return ImmutableList.copyOf(teams.values());
    }

    @Override
    public Optional<ISoccerTeam> getTeamWithMostGoals() {
        // Get the first two teams of the team list
        final ISoccerTeam firstTeam = getTeamList().get(0);
        final ISoccerTeam secondTeam = getTeamList().get(1);

        // Compare both teams using goals, return the winner one. If tie, return empty optional
        if (firstTeam.getGoals() > secondTeam.getGoals()) return Optional.of(firstTeam);
        else if (firstTeam.getGoals() < secondTeam.getGoals()) return Optional.of(secondTeam);
        else return Optional.empty();
    }

    @Override
    public ISoccerTeam getOppositeTeam(ISoccerTeam team) {
        return getTeamList().stream().filter(t -> !t.getIdentifier().equals(team.getIdentifier())).findFirst().orElse(null);
    }

    public List<Cuboid> getGoalCuboids() {
        return getTeamList().stream().map(ISoccerTeam::getGoalCuboid).toList();
    }
}
