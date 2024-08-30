package com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.IBentenganTeamHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BentenganPlayersFromTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull BentenganArena bentenganArena;

    public BentenganPlayersFromTeamAutocompleter(final @NotNull BentenganArena bentenganArena) {
        this.bentenganArena = bentenganArena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final IBentenganTeamHelper teamHelper = bentenganArena.getBentenganMiniGame().getTeamHelper();
        final Optional<IBentenganTeam> bentenganTeamOptional = teamHelper.getTeam(startsWith);
        if (bentenganTeamOptional.isEmpty()) {
            return List.of();
        }

        final List<IBentenganPlayer> playerList = bentenganTeamOptional.get().getPlayers().stream().toList();
        final String lowercase = startsWith.toLowerCase();

        return playerList
                .stream()
                .map(IBentenganPlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}