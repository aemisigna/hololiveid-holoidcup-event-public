package com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class BadmintonPlayerNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BadmintonArena arena;

    public BadmintonPlayerNameAutocompleter(final @NotNull BadmintonArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<IBadmintonPlayer> playerList = arena.getPlayerHelper().getPlayerList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(IBadmintonPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}