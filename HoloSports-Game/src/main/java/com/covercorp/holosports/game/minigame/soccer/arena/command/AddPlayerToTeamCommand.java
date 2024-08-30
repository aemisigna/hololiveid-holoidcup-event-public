package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerPlayersWithoutTeamAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerRoleNameAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerTeamNameAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public final class AddPlayerToTeamCommand extends BukkitCommand<CommandSender> {
    private final SoccerArena soccerArena;

    public AddPlayerToTeamCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:addplayertoteam", "<team> <role> <player>");

        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.addplayertoteam");
        this.setAutocompleter(0, new SoccerTeamNameAutocompleter<>(soccerArena));
        this.setAutocompleter(1, new SoccerRoleNameAutocompleter<>(soccerArena));
        this.setAutocompleter(2, new SoccerPlayersWithoutTeamAutocompleter<>(soccerArena));
        this.setDescription("Add a teamless player to a team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage teams when the game is not started."));
            return;
        }

        final Optional<ISoccerTeam> soccerTeamOptional = soccerArena.getSoccerMiniGame().getTeamHelper().getTeam(parameters.get(0));
        if (soccerTeamOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c does not exist."));
            return;
        }

        final ISoccerTeam soccerTeam = soccerTeamOptional.get();
        if (soccerTeam.getPlayers().size() >= 3) {
            sender.sendMessage(CommonUtil.colorize("&cTeam &e" + parameters.get(0) + "&c is full, please remove a player before adding a new one."));
            return;
        }

        final String roleString = parameters.get(1).toUpperCase();
        if (Arrays.stream(SoccerRole.values()).noneMatch(role -> role.name().equals(roleString))) {
            // Display the roles uppercase and separated by a comma
            final String roleList = Arrays.stream(SoccerRole.values()).map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("");
            sender.sendMessage(CommonUtil.colorize("&cRole &e" + roleString + "&c does not exist. Please use one of the following: &e" + roleList + "&c."));
            return;
        }

        final Player player = parameters.getPlayer(2);
        final Optional<ISoccerPlayer> origSoccerPlayer = soccerArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origSoccerPlayer.isPresent()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c is already in a team."));
            return;
        }

        final Optional<ISoccerPlayer> soccerPlayerOptional = soccerArena.getPlayerHelper().getOrCreatePlayer(player);
        if (soccerPlayerOptional.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cCould not create the player instance. This should not be happening."));
            return;
        }

        final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();

        final SoccerRole role = SoccerRole.valueOf(roleString);
        soccerPlayer.setRole(role);

        soccerArena.getTeamHelper().addPlayerToTeam(soccerPlayer, soccerTeam.getIdentifier());
        SoccerGameItemCollection.setupPlayerHotbar(soccerArena, soccerPlayer, role);

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a has been added to team &e" + soccerTeam.getName() + "&a with the role &e" + roleString + "&a."));
    }
}
