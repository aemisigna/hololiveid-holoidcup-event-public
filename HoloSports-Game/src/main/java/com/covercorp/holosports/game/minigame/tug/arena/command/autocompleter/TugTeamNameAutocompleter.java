package com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class TugTeamNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull TugArena arena;

    public TugTeamNameAutocompleter(final @NotNull TugArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<ITugTeam> teams = arena.getTugMiniGame().getTeamHelper().getTeamList();
        final String lowercase = startsWith.toLowerCase();

        return teams
                .stream()
                .map(ITugTeam::getIdentifier)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}