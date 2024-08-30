package com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class TugPlayersWithTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull TugArena arena;

    public TugPlayersWithTeamAutocompleter(final @NotNull TugArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<ITugPlayer> teamPlayers = arena.getPlayerHelper().getPlayerList();

        final String lowercase = startsWith.toLowerCase();

        return teamPlayers
                .stream()
                .map(ITugPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}