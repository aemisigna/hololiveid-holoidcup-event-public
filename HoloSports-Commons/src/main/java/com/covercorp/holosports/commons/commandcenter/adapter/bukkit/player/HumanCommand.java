package com.covercorp.holosports.commons.commandcenter.adapter.bukkit.player;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import org.bukkit.entity.Player;

public abstract class HumanCommand extends BukkitCommand<Player> {
    public HumanCommand(final String name, final String usage) {
        super(Player.class, name, usage);
    }
}
