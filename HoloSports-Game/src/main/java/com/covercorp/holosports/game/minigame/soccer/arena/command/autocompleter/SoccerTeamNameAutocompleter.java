package com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SoccerTeamNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull SoccerArena arena;

    public SoccerTeamNameAutocompleter(final @NotNull SoccerArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<ISoccerTeam> teams = arena.getSoccerMiniGame().getTeamHelper().getTeamList();
        final String lowercase = startsWith.toLowerCase();

        return teams
                .stream()
                .map(ISoccerTeam::getIdentifier)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}