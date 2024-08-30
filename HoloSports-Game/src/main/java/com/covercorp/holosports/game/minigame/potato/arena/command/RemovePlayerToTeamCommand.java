package com.covercorp.holosports.game.minigame.potato.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter.PotatoPlayersWithTeamAutocompleter;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.inventory.PotatoGameItemCollection;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class RemovePlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final PotatoArena potatoArena;

    public RemovePlayerToTeamCommand(final PotatoArena potatoArena) {
        super(CommandSender.class, "potato:removeplayerfromteam", "<player>");

        this.potatoArena = potatoArena;

        this.setPermission("holosports.potato.removeplayertoteam");
        this.setAutocompleter(0, new PotatoPlayersWithTeamAutocompleter<>(potatoArena));
        this.setDescription("Remove a player from their team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (potatoArena.getState() != PotatoMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Player player = parameters.getPlayer(0);
        final Optional<IPotatoPlayer> origPotatoPlayer = potatoArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origPotatoPlayer.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is not participating in the game."));
            return;
        }

        potatoArena.getTeamHelper().removePlayerFromTeam(origPotatoPlayer.get(), origPotatoPlayer.get().getTeam().getIdentifier());
        PotatoGameItemCollection.resetPlayerHotbar(origPotatoPlayer.get());
        potatoArena.getPlayerHelper().removePlayer(origPotatoPlayer.get().getUniqueId());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been removed from their team."));
    }
}
