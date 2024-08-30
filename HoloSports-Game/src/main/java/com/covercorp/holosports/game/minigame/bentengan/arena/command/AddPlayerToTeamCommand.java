package com.covercorp.holosports.game.minigame.bentengan.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter.BentenganPlayersWithoutTeamAutocompleter;
import com.covercorp.holosports.game.minigame.bentengan.arena.command.autocompleter.BentenganTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class AddPlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final BentenganArena bentenganArena;

    public AddPlayerToTeamCommand(final BentenganArena bentenganArena) {
        super(CommandSender.class, "bentengan:addplayertoteam", "<team> <player>");

        this.bentenganArena = bentenganArena;

        this.setPermission("holosports.bentengan.addplayertoteam");
        this.setAutocompleter(0, new BentenganTeamNameAutocompleter<>(bentenganArena));
        this.setAutocompleter(1, new BentenganPlayersWithoutTeamAutocompleter<>(bentenganArena));
        this.setDescription("Add a teamless player to a team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (bentenganArena.getState() != BentenganMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<IBentenganTeam> bentenganTeamOptional = bentenganArena.getBentenganMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (bentenganTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final IBentenganTeam bentenganTeam = bentenganTeamOptional.get();
        if (bentenganTeam.getPlayers().size() >= 4) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c is full, please remove a player before adding a new one."));
            return;
        }

        final Player player = parameters.getPlayer(1);
        final Optional<IBentenganPlayer> origBentenganPlayer = bentenganArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origBentenganPlayer.isPresent()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is already in a team."));
            return;
        }

        final Optional<IBentenganPlayer> bentenganPlayerOptional = bentenganArena.getPlayerHelper().getOrCreatePlayer(player);
        if (bentenganPlayerOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cCould not create the player instance. This should not be happening."));
            return;
        }

        bentenganArena.getTeamHelper().addPlayerToTeam(bentenganPlayerOptional.get(), bentenganTeam.getIdentifier());
        BentenganGameItemCollection.setupPlayerHotbar(bentenganPlayerOptional.get());

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been added to team &e" + bentenganTeam.getName() + "&a."));
    }
}
