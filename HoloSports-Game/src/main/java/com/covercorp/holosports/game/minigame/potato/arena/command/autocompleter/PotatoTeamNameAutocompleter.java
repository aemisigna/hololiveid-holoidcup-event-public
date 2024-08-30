package com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class PotatoTeamNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull PotatoArena arena;

    public PotatoTeamNameAutocompleter(final @NotNull PotatoArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<IPotatoTeam> teams = arena.getPotatoMiniGame().getTeamHelper().getTeamList();
        final String lowercase = startsWith.toLowerCase();

        return teams
                .stream()
                .map(IPotatoTeam::getIdentifier)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}