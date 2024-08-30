package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter.BadmintonTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public final class ClearTeamPlayersCommand extends BukkitCommand<CommandSender> {
    private final BadmintonArena badmintonArena;

    public ClearTeamPlayersCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:clearteam", "<team>");

        this.badmintonArena = badmintonArena;

        this.setPermission("holosports.badminton.clearteam");
        this.setAutocompleter(0, new BadmintonTeamNameAutocompleter<>(badmintonArena));
        this.setDescription("Clear a team participants.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (badmintonArena.getState() != BadmintonMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<IBadmintonTeam> badmintonTeamOptional = badmintonArena.getBadmintonMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (badmintonTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final IBadmintonTeam badmintonTeam = badmintonTeamOptional.get();

        badmintonTeam.getPlayers().forEach(badmintonPlayer -> {
            badmintonArena.getTeamHelper().removePlayerFromTeam(badmintonPlayer, badmintonTeam.getIdentifier());
            BadmintonGameItemCollection.resetPlayerHotbar(badmintonPlayer);
        });

        sender.sendMessage(CommonUtil.colorize("&aAll team members of team &e" + parameters.get(0) + "&a has been removed from it and they will not be part of the next game."));
    }
}
