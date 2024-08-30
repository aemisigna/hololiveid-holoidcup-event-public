package com.covercorp.holosports.commons.commandcenter.adapter.bukkit.player;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitSubCommand;
import org.bukkit.entity.Player;

public abstract class HumanSubCommand extends BukkitSubCommand<Player> {
    public HumanSubCommand(String name, String hint) {
        super(Player.class, name, hint);
    }
}