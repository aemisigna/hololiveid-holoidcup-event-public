package com.covercorp.holosports.game.minigame.bentengan.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter.BentenganPlayersWithTeamAutocompleter;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class RemovePlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final BentenganArena bentenganArena;

    public RemovePlayerToTeamCommand(final BentenganArena bentenganArena) {
        super(CommandSender.class, "bentengan:removeplayerfromteam", "<player>");

        this.bentenganArena = bentenganArena;

        this.setPermission("holosports.bentengan.removeplayertoteam");
        this.setAutocompleter(0, new BentenganPlayersWithTeamAutocompleter<>(bentenganArena));
        this.setDescription("Remove a player from their team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (bentenganArena.getState() != BentenganMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Player player = parameters.getPlayer(0);
        final Optional<IBentenganPlayer> origBentenganPlayer = bentenganArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origBentenganPlayer.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is not participating in the game."));
            return;
        }

        bentenganArena.getTeamHelper().removePlayerFromTeam(origBentenganPlayer.get(), origBentenganPlayer.get().getTeam().getIdentifier());
        BentenganGameItemCollection.resetPlayerHotbar(origBentenganPlayer.get());
        bentenganArena.getPlayerHelper().removePlayer(origBentenganPlayer.get().getUniqueId());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been removed from their team."));
    }
}
