package com.covercorp.holosports.commons.commandcenter.adapter.bukkit.executor;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import org.bukkit.entity.Player;

public interface BukkitCommandExecutor {
     void execute(Player player, BukkitCommandParameters args) throws CommandFailureException;
}