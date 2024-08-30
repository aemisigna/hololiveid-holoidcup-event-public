package com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SoccerRoleNameAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull SoccerArena arena;

    public SoccerRoleNameAutocompleter(final @NotNull SoccerArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<SoccerRole> roles = Arrays.asList(SoccerRole.values());
        final String lowercase = startsWith.toLowerCase();

        return roles
                .stream()
                .map(SoccerRole::toString)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}