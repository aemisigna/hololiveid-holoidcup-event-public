package com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class BentenganPlayerNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BentenganArena arena;

    public BentenganPlayerNameAutocompleter(final @NotNull BentenganArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<IBentenganPlayer> playerList = arena.getPlayerHelper().getPlayerList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(IBentenganPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}