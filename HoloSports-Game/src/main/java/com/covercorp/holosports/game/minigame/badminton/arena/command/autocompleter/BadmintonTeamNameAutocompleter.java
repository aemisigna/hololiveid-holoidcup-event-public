package com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class BadmintonTeamNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BadmintonArena arena;

    public BadmintonTeamNameAutocompleter(final @NotNull BadmintonArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<IBadmintonTeam> teams = arena.getBadmintonMiniGame().getTeamHelper().getTeamList();
        final String lowercase = startsWith.toLowerCase();

        return teams
                .stream()
                .map(IBadmintonTeam::getIdentifier)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}