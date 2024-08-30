package com.covercorp.holosports.game.minigame.tug.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.inventory.TugGameItemCollection;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
import com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter.TugPlayersWithoutTeamAutocompleter;
import com.covercorp.holosports.game.minigame.tug.arena.command.autocompleter.TugTeamNameAutocompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class AddPlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final TugArena tugArena;

    public AddPlayerToTeamCommand(final TugArena tugArena) {
        super(CommandSender.class, "tug:addplayertoteam", "<team> <player>");

        this.tugArena = tugArena;

        this.setPermission("holosports.tug.addplayertoteam");
        this.setAutocompleter(0, new TugTeamNameAutocompleter<>(tugArena));
        this.setAutocompleter(1, new TugPlayersWithoutTeamAutocompleter<>(tugArena));
        this.setDescription("Add a teamless player to a team.");
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
        if (tugTeam.getPlayers().size() >= 3) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c is full, please remove a player before adding a new one."));
            return;
        }

        final Player player = parameters.getPlayer(1);
        final Optional<ITugPlayer> origTugPlayer = tugArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origTugPlayer.isPresent()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is already in a team."));
            return;
        }

        final Optional<ITugPlayer> tugPlayerOptional = tugArena.getPlayerHelper().getOrCreatePlayer(player);
        if (tugPlayerOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cCould not create the player instance. This should not be happening."));
            return;
        }

        tugArena.getTeamHelper().addPlayerToTeam(tugPlayerOptional.get(), tugTeam.getIdentifier());
        TugGameItemCollection.setupPlayerHotbar(tugPlayerOptional.get());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been added to team &e" + tugTeam.getName() + "&a."));
    }
}
