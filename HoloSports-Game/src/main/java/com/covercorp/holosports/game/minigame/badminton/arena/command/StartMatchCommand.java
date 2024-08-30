package com.covercorp.holosports.game.minigame.badminton.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.arena.task.StartingTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public final class StartMatchCommand extends BukkitCommand<CommandSender> {
    private final BadmintonMiniGame badmintonMiniGame;
    private final BadmintonArena badmintonArena;

    public StartMatchCommand(final BadmintonArena badmintonArena) {
        super(CommandSender.class, "badminton:start", "No specific usage, just the command.");

        this.badmintonMiniGame = badmintonArena.getBadmintonMiniGame();
        this.badmintonArena = badmintonArena;

        this.setPermission("holosports.badminton.stop");
        this.setDescription("Start the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (badmintonArena.getState() != BadmintonMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match has already started!\n "));
            return;
        }

        if (badmintonMiniGame.getPlayerHelper().getPlayerList().size() < 2) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
            return;
        }

        if (badmintonMiniGame.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

            sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
            badmintonMiniGame.getTeamHelper().getTeamList().forEach(team -> {
                if (team.getPlayers().size() == 0) {
                    sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                }
            });
            return;
        }

        if (!badmintonArena.getBadmintonMatchProperties().isStarting()) {
            badmintonArena.getBadmintonMatchProperties().setStarting(true);
            badmintonArena.getBadmintonMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(badmintonMiniGame.getHoloSportsGame(), new StartingTask(badmintonArena), 0L, 20L).getTaskId());

            sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");

            Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

            return;
        }

        Bukkit.getScheduler().cancelTask(badmintonArena.getBadmintonMatchProperties().getStartingTaskId());

        badmintonArena.getBadmintonMatchProperties().resetTimer();

        badmintonArena.setState(BadmintonMatchState.WAITING);

        sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
    }
}