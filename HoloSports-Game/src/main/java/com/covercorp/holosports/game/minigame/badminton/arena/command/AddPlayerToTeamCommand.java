package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter.BadmintonPlayersWithoutTeamAutocompleter;
import com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter.BadmintonTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class AddPlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final BadmintonArena badmintonArena;

    public AddPlayerToTeamCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:addplayertoteam", "<team> <player>");

        this.badmintonArena = badmintonArena;

        this.setPermission("holosports.badminton.addplayertoteam");
        this.setAutocompleter(0, new BadmintonTeamNameAutocompleter<>(badmintonArena));
        this.setAutocompleter(1, new BadmintonPlayersWithoutTeamAutocompleter<>(badmintonArena));
        this.setDescription("Add a teamless player to a team.");
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
        if (badmintonTeam.getPlayers().size() >= 2) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c is full, please remove a player before adding a new one."));
            return;
        }

        final Player player = parameters.getPlayer(1);
        final Optional<IBadmintonPlayer> origBadmintonPlayer = badmintonArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origBadmintonPlayer.isPresent()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is already in a team."));
            return;
        }

        final Optional<IBadmintonPlayer> badmintonPlayerOptional = badmintonArena.getPlayerHelper().getOrCreatePlayer(player);
        if (badmintonPlayerOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cCould not create the player instance. This should not be happening."));
            return;
        }

        badmintonArena.getTeamHelper().addPlayerToTeam(badmintonPlayerOptional.get(), badmintonTeam.getIdentifier());
        BadmintonGameItemCollection.setupPlayerHotbar(badmintonPlayerOptional.get());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been added to team &e" + badmintonTeam.getName() + "&a."));
    }
}
