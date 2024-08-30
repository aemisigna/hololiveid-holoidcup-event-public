package com.covercorp.holosports.game.minigame.potato.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter.PotatoPlayersWithoutTeamAutocompleter;
import com.covercorp.holosports.game.minigame.potato.arena.command.autocompleter.PotatoTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.inventory.PotatoGameItemCollection;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class AddPlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final PotatoArena potatoArena;

    public AddPlayerToTeamCommand(final PotatoArena potatoArena) {
        super(CommandSender.class, "potato:addplayertoteam", "<team> <player>");

        this.potatoArena = potatoArena;

        this.setPermission("holosports.potato.addplayertoteam");
        this.setAutocompleter(0, new PotatoTeamNameAutocompleter<>(potatoArena));
        this.setAutocompleter(1, new PotatoPlayersWithoutTeamAutocompleter<>(potatoArena));
        this.setDescription("Add a teamless player to a team.");
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
        if (potatoTeam.getPlayers().size() >= 3) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c is full, please remove a player before adding a new one."));
            return;
        }

        final Player player = parameters.getPlayer(1);
        final Optional<IPotatoPlayer> origPotatoPlayer = potatoArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origPotatoPlayer.isPresent()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is already in a team."));
            return;
        }

        final Optional<IPotatoPlayer> potatoPlayerOptional = potatoArena.getPlayerHelper().getOrCreatePlayer(player);
        if (potatoPlayerOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cCould not create the player instance. This should not be happening."));
            return;
        }

        potatoArena.getTeamHelper().addPlayerToTeam(potatoPlayerOptional.get(), potatoTeam.getIdentifier());
        PotatoGameItemCollection.setupPlayerHotbar(potatoPlayerOptional.get());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been added to team &e" + potatoTeam.getName() + "&a."));
    }
}
