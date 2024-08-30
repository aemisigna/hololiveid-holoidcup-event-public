package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.command.autocompleter.BadmintonTeamNameAutocompleter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ToggleParticlesCommand extends BukkitCommand<CommandSender> {
    private final BadmintonArena badmintonArena;

    public ToggleParticlesCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:toggleparticles", "No specific usage, just the command.");

        this.badmintonArena = badmintonArena;

        this.setAutocompleter(0, new BadmintonTeamNameAutocompleter<>(badmintonArena));
        this.setDescription("Toggle the in-game helper zone particles");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        final boolean particleMode = badmintonArena.getBadmintonMatchProperties().isParticleMode();;

        // Toggle the particle mode.
        badmintonArena.getBadmintonMatchProperties().setParticleMode(!particleMode);

        final String state = badmintonArena.getBadmintonMatchProperties().isParticleMode() ? "Yes" : "No";
        sender.sendMessage(ChatColor.YELLOW + "[!] Particle mode: " + ChatColor.AQUA + state);
    }
}
