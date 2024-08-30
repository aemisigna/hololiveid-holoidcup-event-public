package com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.IBadmintonTeamHelper;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BadmintonPlayersFromTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BadmintonArena badmintonArena;

    public BadmintonPlayersFromTeamAutocompleter(final @NotNull BadmintonArena badmintonArena) {
        this.badmintonArena = badmintonArena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final IBadmintonTeamHelper teamHelper = badmintonArena.getBadmintonMiniGame().getTeamHelper();
        final Optional<IBadmintonTeam> badmintonTeamOptional = teamHelper.getTeam(startsWith);
        if (badmintonTeamOptional.isEmpty()) {
            return List.of();
        }

        final List<IBadmintonPlayer> playerList = badmintonTeamOptional.get().getPlayers().stream().toList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(IBadmintonPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}