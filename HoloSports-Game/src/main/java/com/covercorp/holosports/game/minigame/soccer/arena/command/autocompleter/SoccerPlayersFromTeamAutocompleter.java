package com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class SoccerPlayersFromTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull SoccerArena soccerArena;

    public SoccerPlayersFromTeamAutocompleter(final @NotNull SoccerArena soccerArena) {
        this.soccerArena = soccerArena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final ISoccerTeamHelper teamHelper = soccerArena.getSoccerMiniGame().getTeamHelper();
        final Optional<ISoccerTeam> soccerTeamOptional = teamHelper.getTeam(startsWith);
        if (soccerTeamOptional.isEmpty()) {
            return List.of();
        }

        final List<ISoccerPlayer> playerList = soccerTeamOptional.get().getPlayers().stream().toList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(ISoccerPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}