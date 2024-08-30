package com.covercorp.holosports.game.minigame.bentengan.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter.BentenganTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;

import org.bukkit.command.CommandSender;

import java.util.Optional;

public final class ClearTeamPlayersCommand extends BukkitCommand<CommandSender> {
    private final BentenganArena bentenganArena;

    public ClearTeamPlayersCommand(final BentenganArena bentenganArena) {
        super(CommandSender.class, "bentengan:clearteam", "<team>");

        this.bentenganArena = bentenganArena;

        this.setPermission("holosports.bentengan.clearteam");
        this.setAutocompleter(0, new BentenganTeamNameAutocompleter<>(bentenganArena));
        this.setDescription("Clear a team participants.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (bentenganArena.getState() != BentenganMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<IBentenganTeam> bentenganTeamOptional = bentenganArena.getBentenganMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (bentenganTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final IBentenganTeam bentenganTeam = bentenganTeamOptional.get();

        bentenganTeam.getPlayers().forEach(bentenganPlayer -> {
            bentenganArena.getTeamHelper().removePlayerFromTeam(bentenganPlayer, bentenganTeam.getIdentifier());
            BentenganGameItemCollection.resetPlayerHotbar(bentenganPlayer);
        });

        sender.sendMessage(CommonUtil.colorize("&aAll team members of team &e" + parameters.get(0) + "&a has been removed from it and they will not be part of the next game."));
    }
}
