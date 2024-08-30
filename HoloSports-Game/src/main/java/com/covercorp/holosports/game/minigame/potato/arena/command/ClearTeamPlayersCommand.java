package com.covercorp.holosports.game.minigame.potato.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter.PotatoTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.inventory.PotatoGameItemCollection;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class ClearTeamPlayersCommand extends BukkitCommand<CommandSender> {
    private final PotatoArena potatoArena;

    public ClearTeamPlayersCommand(final PotatoArena potatoArena) {
        super(CommandSender.class, "potato:clearteam", "<team>");

        this.potatoArena = potatoArena;

        this.setPermission("holosports.potato.clearteam");
        this.setAutocompleter(0, new PotatoTeamNameAutocompleter<>(potatoArena));
        this.setDescription("Clear a team participants.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (potatoArena.getState() != PotatoMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<IPotatoTeam> potatoTeamOptional = potatoArena.getPotatoMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (potatoTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final IPotatoTeam potatoTeam = potatoTeamOptional.get();

        potatoTeam.getPlayers().forEach(potatoPlayer -> {
            potatoArena.getTeamHelper().removePlayerFromTeam(potatoPlayer, potatoTeam.getIdentifier());
            PotatoGameItemCollection.resetPlayerHotbar(potatoPlayer);
        });

        sender.sendMessage(CommonUtil.colorize("&aAll team members of team &e" + parameters.get(0) + "&a has been removed from it and they will not be part of the next game."));
    }
}
