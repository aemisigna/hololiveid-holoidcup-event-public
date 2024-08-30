package com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.ITugTeamHelper;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TugPlayersFromTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull TugArena tugArena;

    public TugPlayersFromTeamAutocompleter(final @NotNull TugArena tugArena) {
        this.tugArena = tugArena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final ITugTeamHelper teamHelper = tugArena.getTugMiniGame().getTeamHelper();
        final Optional<ITugTeam> tugTeamOptional = teamHelper.getTeam(startsWith);
        if (tugTeamOptional.isEmpty()) {
            return List.of();
        }

        final List<ITugPlayer> playerList = tugTeamOptional.get().getPlayers().stream().toList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(ITugPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}