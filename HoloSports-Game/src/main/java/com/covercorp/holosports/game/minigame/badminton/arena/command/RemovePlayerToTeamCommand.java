package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter.BadmintonPlayersWithTeamAutocompleter;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class RemovePlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final BadmintonArena badmintonArena;

    public RemovePlayerToTeamCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:removeplayerfromteam", "<player>");

        this.badmintonArena = badmintonArena;

        this.setPermission("holosports.badminton.removeplayertoteam");
        this.setAutocompleter(0, new BadmintonPlayersWithTeamAutocompleter<>(badmintonArena));
        this.setDescription("Remove a player from their team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (badmintonArena.getState() != BadmintonMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Player player = parameters.getPlayer(0);
        final Optional<IBadmintonPlayer> origBadmintonPlayer = badmintonArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origBadmintonPlayer.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is not participating in the game."));
            return;
        }

        badmintonArena.getTeamHelper().removePlayerFromTeam(origBadmintonPlayer.get(), origBadmintonPlayer.get().getTeam().getIdentifier());
        BadmintonGameItemCollection.resetPlayerHotbar(origBadmintonPlayer.get());

        badmintonArena.getPlayerHelper().removePlayer(origBadmintonPlayer.get().getUniqueId());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been removed from their team."));
    }
}
