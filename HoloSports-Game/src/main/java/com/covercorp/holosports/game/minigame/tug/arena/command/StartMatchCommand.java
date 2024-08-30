package com.covercorp.holosports.game.minigame.tug.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.arena.task.StartingTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public final class StartMatchCommand extends BukkitCommand<CommandSender> {
    private final TugMiniGame tugMiniGame;
    private final TugArena tugArena;

    public StartMatchCommand(final TugArena tugArena) {
        super(CommandSender.class, "tug:start", "No specific usage, just the command.");

        this.tugMiniGame = tugArena.getTugMiniGame();
        this.tugArena = tugArena;

        this.setPermission("holosports.tug.stop");
        this.setDescription("Start the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (tugArena.getState() != TugMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match has already started!\n "));
            return;
        }

        if (tugMiniGame.getPlayerHelper().getPlayerList().size() < 2) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
            return;
        }

        if (tugMiniGame.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

            sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
            tugMiniGame.getTeamHelper().getTeamList().forEach(team -> {
                if (team.getPlayers().size() == 0) {
                    sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                }
            });
            return;
        }

        if (!tugArena.getTugMatchProperties().isStarting()) {
            tugArena.getTugMatchProperties().setStarting(true);
            tugArena.getTugMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(tugMiniGame.getHoloSportsGame(), new StartingTask(tugArena), 0L, 20L).getTaskId());

            sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");

            Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

            return;
        }

        Bukkit.getScheduler().cancelTask(tugArena.getTugMatchProperties().getStartingTaskId());

        tugArena.getTugMatchProperties().resetTimer();

        tugArena.setState(TugMatchState.WAITING);

        sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
    }
}