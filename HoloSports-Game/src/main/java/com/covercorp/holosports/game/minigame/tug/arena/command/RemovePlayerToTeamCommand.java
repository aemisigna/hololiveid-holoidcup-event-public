package com.covercorp.holosports.game.minigame.tug.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.inventory.TugGameItemCollection;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter.TugPlayersWithTeamAutocompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class RemovePlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final TugArena tugArena;

    public RemovePlayerToTeamCommand(final TugArena tugArena) {
        super(CommandSender.class, "tug:removeplayerfromteam", "<player>");

        this.tugArena = tugArena;

        this.setPermission("holosports.tug.removeplayertoteam");
        this.setAutocompleter(0, new TugPlayersWithTeamAutocompleter<>(tugArena));
        this.setDescription("Remove a player from their team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (tugArena.getState() != TugMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Player player = parameters.getPlayer(0);
        final Optional<ITugPlayer> origTugPlayer = tugArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origTugPlayer.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is not participating in the game."));
            return;
        }

        tugArena.getTeamHelper().removePlayerFromTeam(origTugPlayer.get(), origTugPlayer.get().getTeam().getIdentifier());
        TugGameItemCollection.resetPlayerHotbar(origTugPlayer.get());
        tugArena.getPlayerHelper().removePlayer(origTugPlayer.get().getUniqueId());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been removed from their team."));
    }
}
