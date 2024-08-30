package com.covercorp.holosports.game.minigame.bentengan.player;

import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.player.player.BentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BentenganPlayerHelper implements IBentenganPlayerHelper {
    private final BentenganMiniGame bentenganMiniGame;

    private final Map<UUID, IBentenganPlayer> players;

    public BentenganPlayerHelper(final BentenganMiniGame bentenganMiniGame) {
        this.bentenganMiniGame = bentenganMiniGame;

        players = new HashMap<>();
    }

    @Override
    public IBentenganPlayer addPlayer(final Player player) {
        return players.put(player.getUniqueId(), new BentenganPlayer(player.getUniqueId(), player.getName()));
    }

    @Override
    public boolean removePlayer(final UUID uniqueId) {
        final Optional<IBentenganPlayer> playerOptional = getPlayer(uniqueId);
        if (playerOptional.isEmpty()) return false;

        final IBentenganPlayer player = playerOptional.get();
        final IBentenganTeam possibleTeam = player.getTeam();

        if (possibleTeam != null) possibleTeam.removePlayer(player);

        players.remove(uniqueId);

        return true;
    }

    @Override
    public Optional<IBentenganPlayer> getPlayer(final UUID uniqueId) {
        return Optional.ofNullable(players.get(uniqueId));
    }

    @Override
    public Optional<IBentenganPlayer> getOrCreatePlayer(Player player) {
        // I don't know what the fuck I did here and ik, it makes not sense, but it works...?
        final Optional<IBentenganPlayer> bentenganPlayerOptional = getPlayer(player.getUniqueId());
        if (bentenganPlayerOptional.isPresent()) return bentenganPlayerOptional;

        addPlayer(player);

        return getPlayer(player.getUniqueId());
    }

    @Override
    public ImmutableList<IBentenganPlayer> getPlayerList() {
        return ImmutableList.copyOf(players.values());
    }

    @Override
    public void clearPlayerList() {
        getPlayerList().forEach(player -> {
            if (!removePlayer(player.getUniqueId())) {
                bentenganMiniGame.getHoloSportsGame().getLogger().severe("Could not unregister player: " + player.getUniqueId());
                return;
            }

            bentenganMiniGame.getHoloSportsGame().getLogger().severe("Unregistered player: " + player.getUniqueId());
        });
    }
}
