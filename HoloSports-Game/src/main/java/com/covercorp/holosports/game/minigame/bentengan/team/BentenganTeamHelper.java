package com.covercorp.holosports.game.minigame.bentengan.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import com.covercorp.holosports.game.minigame.bentengan.team.team.BentenganTeam;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.config.BentenganConfigHelper;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BentenganTeamHelper implements IBentenganTeamHelper {
    private final BentenganMiniGame bentenganMiniGame;

    private final Map<String, IBentenganTeam> teams;

    public BentenganTeamHelper(final BentenganMiniGame bentenganMiniGame) {
        this.bentenganMiniGame = bentenganMiniGame;

        teams = new HashMap<>();
    }

    @Override
    public boolean registerTeam(final String identifier, final String display, final String color, final Location spawnPoint, final Cuboid zone, final Location jailSpawnPoint, final Cuboid jailZone, final Cuboid beaconZone) {
        try {
            final IBentenganTeam bentenganTeam = new BentenganTeam(identifier, display, color, spawnPoint, zone, jailSpawnPoint, jailZone, beaconZone);

            teams.put(identifier, bentenganTeam);

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void registerTeams() {
        final BentenganConfigHelper bentenganConfigHelper = bentenganMiniGame.getBentenganConfigHelper();

        bentenganConfigHelper.getTeamIdentifiers().forEach(teamId -> {
            final boolean res = registerTeam(
                    teamId,
                    bentenganConfigHelper.getTeamDisplay(teamId),
                    bentenganConfigHelper.getTeamColor(teamId),
                    bentenganConfigHelper.getTeamSpawnPoint(teamId),
                    bentenganConfigHelper.getTeamZone(teamId),
                    bentenganConfigHelper.getTeamJailSpawnPoint(teamId),
                    bentenganConfigHelper.getTeamJailZone(teamId),
                    bentenganConfigHelper.getTeamBeaconZone(teamId)
            );

            if (!res) {
                bentenganMiniGame.getHoloSportsGame().getLogger().severe("Could not register team: " + teamId);
                return;
            }

            bentenganMiniGame.getHoloSportsGame().getLogger().info("Registered Team: " + teamId);
        });
    }

    @Override
    public boolean unregisterTeam(final String teamIdentifier) {
        final Optional<IBentenganTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IBentenganTeam team = teamOptional.get();

        teams.remove(team.getIdentifier());

        return true;
    }

    @Override
    public void unregisterTeams() {
        getTeamList().forEach(team -> {
            final boolean res = unregisterTeam(team.getIdentifier());

            if (!res) {
                bentenganMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister team: " + team.getIdentifier());
                return;
            }

            bentenganMiniGame.getHoloSportsGame().getLogger().info("Unregistered team: " + team.getIdentifier());
        });
    }

    @Override
    public boolean addPlayerToTeam(final IBentenganPlayer player, final String teamIdentifier) {
        if (player.getTeam() != null) removePlayerFromTeam(player, player.getTeam().getIdentifier());

        final Optional<IBentenganTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IBentenganTeam team = teamOptional.get();

        team.addPlayer(player);
        player.setTeam(team);

        bentenganMiniGame.getHoloSportsGame().getLogger().info("Added player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public boolean removePlayerFromTeam(final IBentenganPlayer player, final String teamIdentifier) {
        final Optional<IBentenganTeam> teamOptional = getTeam(teamIdentifier);
        if (teamOptional.isEmpty()) return false;

        final IBentenganTeam team = teamOptional.get();

        team.removePlayer(player);
        player.setTeam(null);

        bentenganMiniGame.getHoloSportsGame().getLogger().info("Removed player " + player.getName() + " from team " + teamIdentifier);

        return true;
    }

    @Override
    public Optional<IBentenganTeam> getTeam(final String teamIdentifier) {
        return Optional.ofNullable(teams.get(teamIdentifier));
    }

    @Override
    public ImmutableList<IBentenganTeam> getTeamList() {
        return ImmutableList.copyOf(teams.values());
    }

    @Override
    public IBentenganTeam getOppositeTeam(IBentenganTeam team) {
        return getTeamList().stream().filter(t -> !t.getIdentifier().equals(team.getIdentifier())).findFirst().orElse(null);
    }
}
