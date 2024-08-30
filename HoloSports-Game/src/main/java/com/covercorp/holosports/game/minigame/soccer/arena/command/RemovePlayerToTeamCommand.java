package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerPlayersWithTeamAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class RemovePlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final SoccerArena soccerArena;

    public RemovePlayerToTeamCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:removeplayerfromteam", "<player>");

        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.removeplayertoteam");
        this.setAutocompleter(0, new SoccerPlayersWithTeamAutocompleter<>(soccerArena));
        this.setDescription("Remove a player from their team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Player player = parameters.getPlayer(0);
        final Optional<ISoccerPlayer> origSoccerPlayer = soccerArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origSoccerPlayer.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is not participating in the game."));
            return;
        }

        soccerArena.getTeamHelper().removePlayerFromTeam(origSoccerPlayer.get(), origSoccerPlayer.get().getTeam().getIdentifier());
        origSoccerPlayer.get().setRole(null);

        SoccerGameItemCollection.resetPlayerHotbar(origSoccerPlayer.get());
        soccerArena.getPlayerHelper().removePlayer(origSoccerPlayer.get().getUniqueId());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been removed from their team and their role has been reset."));
    }
}
