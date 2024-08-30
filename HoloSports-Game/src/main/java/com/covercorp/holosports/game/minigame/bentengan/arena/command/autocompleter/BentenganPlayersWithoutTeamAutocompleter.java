package com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BentenganPlayersWithoutTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BentenganArena arena;

    public BentenganPlayersWithoutTeamAutocompleter(final @NotNull BentenganArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        final List<String> noTeamPlayers = new ArrayList<>();

        onlinePlayers.forEach(player -> {
            final Optional<IBentenganPlayer> tugPlayer = arena.getBentenganMiniGame().getPlayerHelper().getPlayer(player.getUniqueId());
            if (tugPlayer.isEmpty()) {
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