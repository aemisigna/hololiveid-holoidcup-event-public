package com.covercorp.holosports.game.minigame.tug.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.inventory.TugGameItemCollection;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter.TugTeamNameAutocompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class ClearTeamPlayersCommand extends BukkitCommand<CommandSender> {
    private final TugArena tugArena;

    public ClearTeamPlayersCommand(final TugArena tugArena) {
        super(CommandSender.class, "tug:clearteam", "<team>");

        this.tugArena = tugArena;

        this.setPermission("holosports.tug.clearteam");
        this.setAutocompleter(0, new TugTeamNameAutocompleter<>(tugArena));
        this.setDescription("Clear a team participants.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (tugArena.getState() != TugMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<ITugTeam> tugTeamOptional = tugArena.getTugMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (tugTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final ITugTeam tugTeam = tugTeamOptional.get();

        tugTeam.getPlayers().forEach(tugPlayer -> {
            tugArena.getTeamHelper().removePlayerFromTeam(tugPlayer, tugTeam.getIdentifier());
            TugGameItemCollection.resetPlayerHotbar(tugPlayer);
        });

        sender.sendMessage(CommonUtil.colorize("&aAll team members of team &e" + parameters.get(0) + "&a has been removed from it and they will not be part of the next game."));
    }
}
