package com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class SoccerPlayersWithoutTeamAutocompleter<T> implements Autocompleter<T> {
    private final @NotNull SoccerArena arena;

    public SoccerPlayersWithoutTeamAutocompleter(final @NotNull SoccerArena arena) {
        this.arena = arena;
    }

    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        final List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        final List<String> noTeamPlayers = new ArrayList<>();

        onlinePlayers.forEach(player -> {
            final Optional<ISoccerPlayer> soccerPlayer = arena.getSoccerMiniGame().getPlayerHelper().getPlayer(player.getUniqueId());
            if (soccerPlayer.isEmpty()) {
                noTeamPlayers.add(player.getName());
            }
        });

        final String lowercase = startsWith.toLowerCase();

        return noTeamPlayers
                .stream()
                .filter(name -> name.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}