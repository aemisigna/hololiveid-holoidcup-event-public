package com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PotatoPlayersWithoutTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull PotatoArena arena;

    public PotatoPlayersWithoutTeamAutocompleter(final @NotNull PotatoArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        final List<String> noTeamPlayers = new ArrayList<>();

        onlinePlayers.forEach(player -> {
            final Optional<IPotatoPlayer> potatoPlayer = arena.getPotatoMiniGame().getPlayerHelper().getPlayer(player.getUniqueId());
            if (potatoPlayer.isEmpty()) {
                noTeamPlayers.add(player.getName());
            }
        });

        final String lowercase = startsWith.toLowerCase();

        return noTeamPlayers
                .stream()
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}