package com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class BentenganTeamNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BentenganArena arena;

    public BentenganTeamNameAutocompleter(final @NotNull BentenganArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<IBentenganTeam> teams = arena.getBentenganMiniGame().getTeamHelper().getTeamList();
        final String lowercase = startsWith.toLowerCase();

        return teams
                .stream()
                .map(IBentenganTeam::getIdentifier)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}