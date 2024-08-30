package com.covercorp.holosports.game.minigame.badminton.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.config.BadmintonConfigHelper;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.BadmintonTeam;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BadmintonTeamHelper implements IBadmintonTeamHelper {
    private final BadmintonMiniGame badmintonMiniGame;

    private final Map<String, IBadmintonTeam> teams;

    public BadmintonTeamHelper(final BadmintonMiniGame badmintonMiniGame) {
        this.badmintonMiniGame = badmintonMiniGame;

        teams = new HashMap<>();
    }

    @Override
    public boolean registerTeam(
            final String identifier,
            final String display,
            final String color,
            final Cuboid singlePointZone,
            final Cuboid doublePointZone,
            final Cuboid singleServePointZoneEven,
            final Cuboid singleServePointZoneOdd,
            final Cuboid doubleServePointZoneEven,
            final Cuboid doubleServePointZoneOdd,
            final Location singleServePositionEven,
            final Location singleServePositionOdd,
            final Location doubleServePositionEven,
            final Location doubleServePositionOdd,
            final Location shuttlecockSpawnEven,
            final Location shuttlecockSpawnOdd,
            final Location shuttlecockSpawnNormal,
            final List<Location> standardSpawns) {
        try {
            final IBadmintonTeam badmintonTeam = new BadmintonTeam(identifier, display, color, singlePointZone, doublePointZone, singleServePointZoneEven, singleServePointZoneOdd, doubleServePointZoneEven, doubleServePointZoneOdd, singleServePositionEven, singleServePositionOdd, doubleServePositionEven, doubleServePositionOdd, shuttlecockSpawnEven, shuttlecockSpawnOdd, shuttlecockSpawnNormal, standardSpawns);

            teams.put(identifier, badmintonTeam);

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void registerTeams() {
        final BadmintonConfigHelper badmintonConfigHelper = badmintonMiniGame.getBadmintonConfigHelper();

        badmintonConfigHelper.getTeamIdentifiers().forEach(teamId -> {
            final boolean res = registerTeam(
                    teamId,
                    badmintonConfigHelper.getTeamDisplay(teamId),
                    badmintonConfigHelper.getTeamColor(teamId),
                    badmintonConfigHelper.getTeamSinglePointZone(teamId),
                    badmintonConfigHelper.getTeamDoublePointZone(teamId),
                    badmintonConfigHelper.getTeamSingleServePointZoneEven(teamId),
                    badmintonConfigHelper.getTeamSingleServePointZoneOdd(teamId),
                    badmintonConfigHelper.getTeamDoubleServePointZoneEven(teamId),
                    badmintonConfigHelper.getTeamDoubleServePointZoneOdd(teamId),
                    badmintonConfigHelper.getSingleServePositionEven(teamId),
                    badmintonConfigHelper.getSingleServePositionOdd(teamId),
                    badmintonConfigHelper.getDoubleServePositionEven(teamId),
                    badmintonConfigHelper.getDoubleServePositionOdd(teamId),
                    badmintonConfigHelper.getShuttlecockSpawnEven(teamId),
                    badmintonConfigHelper.getShuttlecockSpawnOdd(teamId),
                    badmintonConfigHelper.getShuttlecockSpawnNormal(teamId),
                    badmintonConfigHelper.getTeamStandardSpawns(teamId)
            );

            if (!res) {
                badmintonMiniGame.getHoloSportsGame().getLogger().severe("Could not register team: " + teamId);
                return;
            }

            badmintonMiniGame.getHoloSportsGame().getLogger().info("Registered Team: " + teamId);
        });
    }

    @Override
    public boolean unregisterTeam(final String teamIdentifier) {
        final Optional<IBadmintonTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IBadmintonTeam team = teamOptional.get();

        teams.remove(team.getIdentifier());

        return true;
    }

    @Override
    public void unregisterTeams() {
        getTeamList().forEach(team -> {
            final boolean res = unregisterTeam(team.getIdentifier());

            if (!res) {
                badmintonMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister team: " + team.getIdentifier());
                return;
            }

            badmintonMiniGame.getHoloSportsGame().getLogger().info("Unregistered team: " + team.getIdentifier());
        });
    }

    @Override
    public boolean addPlayerToTeam(final IBadmintonPlayer player, final String teamIdentifier) {
        if (player.getTeam() != null) removePlayerFromTeam(player, player.getTeam().getIdentifier());

        final Optional<IBadmintonTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IBadmintonTeam team = teamOptional.get();

        team.addPlayer(player);
        player.setTeam(team);

        badmintonMiniGame.getHoloSportsGame().getLogger().info("Added player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public boolean removePlayerFromTeam(final IBadmintonPlayer player, final String teamIdentifier) {
        final Optional<IBadmintonTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IBadmintonTeam team = teamOptional.get();

        team.removePlayer(player);
        player.setTeam(null);

        badmintonMiniGame.getHoloSportsGame().getLogger().info("Removed player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public Optional<IBadmintonTeam> getTeam(final String teamIdentifier) {
        return Optional.ofNullable(teams.get(teamIdentifier));
    }

    @Override
    public ImmutableList<IBadmintonTeam> getTeamList() {
        return ImmutableList.copyOf(teams.values());
    }

    @Override
    public Optional<IBadmintonTeam> getTeamWithMostPoints() {
        // Get the first two teams of the team list
        final IBadmintonTeam firstTeam = getTeamList().get(0);
        final IBadmintonTeam secondTeam = getTeamList().get(1);

        // Compare both teams using goals, return the winner one. If tie, return empty optional
        if (firstTeam.getPoints() > secondTeam.getPoints()) return Optional.of(firstTeam);
        else if (firstTeam.getPoints() < secondTeam.getPoints()) return Optional.of(secondTeam);
        else return Optional.empty();
    }

    @Override
    public IBadmintonTeam getOppositeTeam(IBadmintonTeam team) {
        return getTeamList().stream().filter(t -> !t.getIdentifier().equals(team.getIdentifier())).findFirst().orElse(null);
    }
}
