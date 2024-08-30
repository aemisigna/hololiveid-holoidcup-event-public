package com.covercorp.holosports.game.minigame.tug.player;

import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.player.player.TugPlayer;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class TugPlayerHelper implements ITugPlayerHelper {
    private final TugMiniGame tugMiniGame;

    private final Map<UUID, ITugPlayer> players;

    public TugPlayerHelper(final TugMiniGame tugMiniGame) {
        this.tugMiniGame = tugMiniGame;

        players = new HashMap<>();
    }

    @Override
    public ITugPlayer addPlayer(final Player player) {
        return players.put(player.getUniqueId(), new TugPlayer(player.getUniqueId(), player.getName()));
    }

    @Override
    public boolean removePlayer(final UUID uniqueId) {
        final Optional<ITugPlayer> playerOptional = getPlayer(uniqueId);
        if (playerOptional.isEmpty()) return false;

        final ITugPlayer player = playerOptional.get();
        final ITugTeam possibleTeam = player.getTeam();

        if (possibleTeam != null) possibleTeam.removePlayer(player);

        players.remove(uniqueId);

        return true;
    }

    @Override
    public Optional<ITugPlayer> getPlayer(final UUID uniqueId) {
        return Optional.ofNullable(players.get(uniqueId));
    }

    @Override
    public Optional<ITugPlayer> getOrCreatePlayer(Player player) {
        // I don't know what the fuck I did here and ik, it makes not sense, but it works...?
        final Optional<ITugPlayer> soccerPlayerOptional = getPlayer(player.getUniqueId());
        if (soccerPlayerOptional.isPresent()) return soccerPlayerOptional;

        addPlayer(player);

        return getPlayer(player.getUniqueId());
    }

    @Override
    public ImmutableList<ITugPlayer> getPlayerList() {
        return ImmutableList.copyOf(players.values());
    }

    @Override
    public void clearPlayerList() {
        getPlayerList().forEach(player -> {
            if (!removePlayer(player.getUniqueId())) {
                tugMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister player: " + player.getUniqueId());
                return;
            }

            tugMiniGame.getHoloSportsGame().getLogger().severe("Unregistered player: " + player.getUniqueId());
        });
    }
}
