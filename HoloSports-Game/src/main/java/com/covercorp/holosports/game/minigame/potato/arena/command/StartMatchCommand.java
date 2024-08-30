package com.covercorp.holosports.game.minigame.potato.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.arena.task.StartingTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public final class StartMatchCommand extends BukkitCommand<CommandSender> {
    private final PotatoMiniGame potatoMiniGame;
    private final PotatoArena potatoArena;

    public StartMatchCommand(final PotatoArena potatoArena) {
        super(CommandSender.class, "potato:start", "No specific usage, just the command.");

        this.potatoMiniGame = potatoArena.getPotatoMiniGame();
        this.potatoArena = potatoArena;

        this.setPermission("holosports.potato.stop");
        this.setDescription("Start the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (potatoArena.getState() != PotatoMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match has already started!\n "));
            return;
        }

        if (potatoMiniGame.getPlayerHelper().getPlayerList().size() < 2) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
            return;
        }

        if (potatoMiniGame.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

            sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
            potatoMiniGame.getTeamHelper().getTeamList().forEach(team -> {
                if (team.getPlayers().size() == 0) {
                    sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                }
            });
            return;
        }

        if (!potatoArena.getPotatoMatchProperties().isStarting()) {
            potatoArena.getPotatoMatchProperties().setStarting(true);
            potatoArena.getPotatoMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(potatoMiniGame.getHoloSportsGame(), new StartingTask(potatoArena), 0L, 20L).getTaskId());

            sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");

            Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

            return;
        }

        Bukkit.getScheduler().cancelTask(potatoArena.getPotatoMatchProperties().getStartingTaskId());

        potatoArena.getPotatoMatchProperties().resetTimer();

        potatoArena.setState(PotatoMatchState.WAITING);

        sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
    }
}