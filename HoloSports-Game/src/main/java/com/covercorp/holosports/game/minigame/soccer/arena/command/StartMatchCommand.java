package com.covercorp.holosports.game.minigame.soccer.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.arena.task.StartingTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public final class StartMatchCommand extends BukkitCommand<CommandSender> {
    private final SoccerMiniGame soccerMiniGame;
    private final SoccerArena soccerArena;

    public StartMatchCommand(final SoccerArena soccerArena) {
        super(CommandSender.class, "soccer:start", "No specific usage, just the command.");

        this.soccerMiniGame = soccerArena.getSoccerMiniGame();
        this.soccerArena = soccerArena;

        this.setPermission("holosports.soccer.stop");
        this.setDescription("Start the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (soccerArena.getState() != SoccerMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match has already started!\n "));
            return;
        }

        if (soccerMiniGame.getPlayerHelper().getPlayerList().stream().filter(p -> !p.isReferee()).toList().size() < 2) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
            return;
        }

        if (soccerMiniGame.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

            sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
            soccerMiniGame.getTeamHelper().getTeamList().forEach(team -> {
                if (team.getPlayers().size() == 0) {
                    sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                }
            });
            return;
        }

        if (soccerMiniGame.getPlayerHelper().noRolePlayers().size() != 0) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are players without a role!\n "));

            sender.sendMessage(CommonUtil.colorize("&7The following participants don't have a role:"));
            soccerMiniGame.getPlayerHelper().noRolePlayers().forEach(soccerPlayer -> {
                sender.sendMessage(CommonUtil.colorize("&8- &f" + soccerPlayer.getName() + " &7[Team " + ChatColor.valueOf(soccerPlayer.getTeam().getColor()) + soccerPlayer.getTeam().getName() + "&7]"));
            });
            return;
        }


        if (!soccerArena.getSoccerMatchProperties().isStarting()) {
            soccerArena.getSoccerMatchProperties().setStarting(true);
            soccerArena.getSoccerMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(soccerMiniGame.getHoloSportsGame(), new StartingTask(soccerArena), 0L, 20L).getTaskId());

            sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");

            Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

            return;
        }

        Bukkit.getScheduler().cancelTask(soccerArena.getSoccerMatchProperties().getStartingTaskId());

        soccerArena.getSoccerMatchProperties().resetTimer();

        soccerArena.setState(SoccerMatchState.WAITING);

        sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
    }
}