package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerPlayersWithTeamAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.command.autocompleter.SoccerRoleNameAutocompleter;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public final class SetPlayerRoleCommand extends BukkitCommand<CommandSender> {
    private final SoccerArena soccerArena;

    public SetPlayerRoleCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:setplayerrole", "<role> <player>");

        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.setplayerrole");
        this.setAutocompleter(0, new SoccerRoleNameAutocompleter<>(soccerArena));
        this.setAutocompleter(1, new SoccerPlayersWithTeamAutocompleter<>(soccerArena));
        this.setDescription("Add a player without role to a team.");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&cYou can only manage roles when the game is not started."));
            return;
        }

        final String roleString = parameters.get(0).toUpperCase();
        if (Arrays.stream(SoccerRole.values()).noneMatch(role -> role.name().equals(roleString))) {
            // Display the roles uppercase and separated by a comma
            final String roleList = Arrays.stream(SoccerRole.values()).map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("");
            sender.sendMessage(CommonUtil.colorize("&cRole &e" + roleString + "&c does not exist. Please use one of the following: &e" + roleList + "&c."));
            return;
        }

        final Player player = parameters.getPlayer(1);
        final Optional<ISoccerPlayer> origSoccerPlayer = soccerArena.getPlayerHelper().getPlayer(player.getUniqueId());
        if (origSoccerPlayer.isEmpty()) {
            sender.sendMessage(CommonUtil.colorize("&cPlayer &e" + player.getName() + "&c doesn't have any team, please use the /soccer:addtoteam command instead."));
            return;
        }

        final ISoccerPlayer soccerPlayer = origSoccerPlayer.get();

        final SoccerRole role = SoccerRole.valueOf(roleString);
        soccerPlayer.setRole(role);

        SoccerGameItemCollection.setupPlayerHotbar(soccerArena, soccerPlayer, role);

        sender.sendMessage(CommonUtil.colorize("&aPlayer &e" + player.getName() + "&a role has been set to &e" + roleString + "&a."));
    }
}
