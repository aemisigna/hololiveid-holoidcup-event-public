package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import org.bukkit.command.CommandSender;

import java.util.Optional;

public final class ClearTeamPlayersCommand extends BukkitCommand<CommandSender> {
    private final SoccerArena soccerArena;

    public ClearTeamPlayersCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:clearteam", "<team>");

        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.clearteam");
        this.setAutocompleter(0, new SoccerTeamNameAutocompleter<>(soccerArena));
        this.setDescription("Clear a team participants.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<ISoccerTeam> soccerTeamOptional = soccerArena.getSoccerMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (soccerTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final ISoccerTeam soccerTeam = soccerTeamOptional.get();

        soccerTeam.getPlayers().forEach(soccerPlayer -> {
            soccerPlayer.setRole(null);
            soccerArena.getTeamHelper().removePlayerFromTeam(soccerPlayer, soccerTeam.getIdentifier());

            SoccerGameItemCollection.resetPlayerHotbar(soccerPlayer);
        });

        sender.sendMessage(CommonUtil.colorize("&aAll team members of team &e" + parameters.get(0) + "&a has been removed from it, their role has been reset and they will not be part of the next game."));
    }
}
