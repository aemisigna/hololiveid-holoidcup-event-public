package com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SoccerPlayersWithTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull SoccerArena arena;

    public SoccerPlayersWithTeamAutocompleter(final @NotNull SoccerArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<ISoccerPlayer> teamPlayers = arena.getPlayerHelper().getPlayerList();

        final String lowercase = startsWith.toLowerCase();

        return teamPlayers
                .stream()
                .map(ISoccerPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}