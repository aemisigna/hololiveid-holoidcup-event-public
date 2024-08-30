package com.covercorp.holosports.game.minigame.badminton.player;

import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.player.player.BadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BadmintonPlayerHelper implements IBadmintonPlayerHelper {
    private final BadmintonMiniGame badmintonMiniGame;

    private final Map<UUID, IBadmintonPlayer> players;

    public BadmintonPlayerHelper(final BadmintonMiniGame badmintonMiniGame) {
        this.badmintonMiniGame = badmintonMiniGame;

        players = new HashMap<>();
    }

    @Override
    public IBadmintonPlayer addPlayer(final Player player) {
        return players.put(player.getUniqueId(), new BadmintonPlayer(player.getUniqueId(), player.getName()));
    }

    @Override
    public boolean removePlayer(final UUID uniqueId) {
        final Optional<IBadmintonPlayer> playerOptional = getPlayer(uniqueId);
        if (playerOptional.isEmpty()) return false;

        final IBadmintonPlayer player = playerOptional.get();
        final IBadmintonTeam possibleTeam = player.getTeam();

        if (possibleTeam != null) possibleTeam.removePlayer(player);

        players.remove(uniqueId);

        return true;
    }

    @Override
    public Optional<IBadmintonPlayer> getPlayer(final UUID uniqueId) {
        return Optional.ofNullable(players.get(uniqueId));
    }

    @Override
    public Optional<IBadmintonPlayer> getOrCreatePlayer(Player player) {
        // I don't know what the fuck I did here and ik, it makes not sense, but it works...?
        final Optional<IBadmintonPlayer> soccerPlayerOptional = getPlayer(player.getUniqueId());
        if (soccerPlayerOptional.isPresent()) return soccerPlayerOptional;

        addPlayer(player);

        return getPlayer(player.getUniqueId());
    }

    @Override
    public ImmutableList<IBadmintonPlayer> getPlayerList() {
        return ImmutableList.copyOf(players.values());
    }

    @Override
    public void clearPlayerList() {
        getPlayerList().forEach(player -> {
            if (!removePlayer(player.getUniqueId())) {
                badmintonMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister player: " + player.getUniqueId());
                return;
            }

            badmintonMiniGame.getHoloSportsGame().getLogger().severe("Unregistered player: " + player.getUniqueId());
        });
    }
}
