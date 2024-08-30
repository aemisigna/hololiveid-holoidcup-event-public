package com.covercorp.holosports.game.minigame.soccer.player;

import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;

import com.covercorp.holosports.game.minigame.soccer.player.player.SoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class SoccerPlayerHelper implements ISoccerPlayerHelper {
    private final SoccerMiniGame soccerMiniGame;

    private final Map<UUID, ISoccerPlayer> players;

    public SoccerPlayerHelper(final SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        players = new HashMap<>();
    }

    @Override
    public ISoccerPlayer addPlayer(final Player player) {
        return players.put(player.getUniqueId(), new SoccerPlayer(player.getUniqueId(), player.getName()));
    }

    @Override
    public boolean removePlayer(final UUID uniqueId) {
        final Optional<ISoccerPlayer> playerOptional = getPlayer(uniqueId);
        if (playerOptional.isEmpty()) return false;

        final ISoccerPlayer player = playerOptional.get();
        final ISoccerTeam possibleTeam = player.getTeam();

        if (possibleTeam != null) possibleTeam.removePlayer(player);

        players.remove(uniqueId);

        return true;
    }

    @Override
    public Optional<ISoccerPlayer> getPlayer(final UUID uniqueId) {
        return Optional.ofNullable(players.get(uniqueId));
    }

    @Override
    public Optional<ISoccerPlayer> getOrCreatePlayer(Player player) {
        // I don't know what the fuck I did here and ik, it makes not sense, but it works...?
        final Optional<ISoccerPlayer> soccerPlayerOptional = getPlayer(player.getUniqueId());
        if (soccerPlayerOptional.isPresent()) return soccerPlayerOptional;

        addPlayer(player);

        return getPlayer(player.getUniqueId());
    }

    @Override
    public ImmutableList<ISoccerPlayer> getPlayerList() {
        return ImmutableList.copyOf(players.values());
    }

    @Override
    public void clearPlayerList() {
        getPlayerList().forEach(player -> {
            if (!removePlayer(player.getUniqueId())) {
                soccerMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister player: " + player.getUniqueId());
                return;
            }

            soccerMiniGame.getHoloSportsGame().getLogger().severe("Unregistered player: " + player.getUniqueId());
        });
    }

    @Override
    public ImmutableList<ISoccerPlayer> noRolePlayers() {
        return ImmutableList.copyOf(getPlayerList().stream().filter(soccerPlayer -> soccerPlayer.getRole() == null).filter(soccerPlayer -> !soccerPlayer.isReferee()).toList());
    }
}
