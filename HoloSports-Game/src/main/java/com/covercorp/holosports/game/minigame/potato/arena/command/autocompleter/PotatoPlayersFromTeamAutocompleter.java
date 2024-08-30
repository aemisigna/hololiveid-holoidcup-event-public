package com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.IPotatoTeamHelper;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PotatoPlayersFromTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull PotatoArena potatoArena;

    public PotatoPlayersFromTeamAutocompleter(final @NotNull PotatoArena potatoArena) {
        this.potatoArena = potatoArena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final IPotatoTeamHelper teamHelper = potatoArena.getPotatoMiniGame().getTeamHelper();
        final Optional<IPotatoTeam> potatoTeamOptional = teamHelper.getTeam(startsWith);
        if (potatoTeamOptional.isEmpty()) {
            return List.of();
        }

        final List<IPotatoPlayer> playerList = potatoTeamOptional.get().getPlayers().stream().toList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(IPotatoPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}