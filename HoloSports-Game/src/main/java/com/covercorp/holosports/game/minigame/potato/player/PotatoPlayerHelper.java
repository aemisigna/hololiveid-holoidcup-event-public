package com.covercorp.holosports.game.minigame.potato.player;

import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.player.player.PotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PotatoPlayerHelper implements IPotatoPlayerHelper {
    private final PotatoMiniGame potatoMiniGame;

    private final Map<UUID, IPotatoPlayer> players;

    public PotatoPlayerHelper(final PotatoMiniGame potatoMiniGame) {
        this.potatoMiniGame = potatoMiniGame;

        players = new HashMap<>();
    }

    @Override
    public IPotatoPlayer addPlayer(final Player player) {
        return players.put(player.getUniqueId(), new PotatoPlayer(player.getUniqueId(), player.getName()));
    }

    @Override
    public boolean removePlayer(final UUID uniqueId) {
        final Optional<IPotatoPlayer> playerOptional = getPlayer(uniqueId);
        if (playerOptional.isEmpty()) return false;

        final IPotatoPlayer player = playerOptional.get();
        final IPotatoTeam possibleTeam = player.getTeam();

        if (possibleTeam != null) possibleTeam.removePlayer(player);

        players.remove(uniqueId);

        return true;
    }

    @Override
    public Optional<IPotatoPlayer> getPlayer(final UUID uniqueId) {
        return Optional.ofNullable(players.get(uniqueId));
    }

    @Override
    public Optional<IPotatoPlayer> getOrCreatePlayer(Player player) {
        // I don't know what the fuck I did here and ik, it makes not sense, but it works...?
        final Optional<IPotatoPlayer> soccerPlayerOptional = getPlayer(player.getUniqueId());
        if (soccerPlayerOptional.isPresent()) return soccerPlayerOptional;

        addPlayer(player);

        return getPlayer(player.getUniqueId());
    }

    @Override
    public ImmutableList<IPotatoPlayer> getPlayerList() {
        return ImmutableList.copyOf(players.values());
    }

    @Override
    public void clearPlayerList() {
        getPlayerList().forEach(player -> {
            if (!removePlayer(player.getUniqueId())) {
                potatoMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister player: " + player.getUniqueId());
                return;
            }

            potatoMiniGame.getHoloSportsGame().getLogger().severe("Unregistered player: " + player.getUniqueId());
        });
    }
}
