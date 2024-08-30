package com.covercorp.holosports.game.minigame.bentengan.arena.command;

import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.BukkitCommand;
import com.covercorp.holosports.commons.commandcenter.adapter.bukkit.argument.BukkitCommandParameters;
import com.covercorp.holosports.commons.commandcenter.exception.CommandFailureException;
import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.arena.task.StartingTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public final class StartMatchCommand extends BukkitCommand<CommandSender> {
    private final BentenganMiniGame bentenganMiniGame;
    private final BentenganArena bentenganArena;

    public StartMatchCommand(final BentenganArena bentenganArena) {
        super(CommandSender.class, "bentengan:start", "No specific usage, just the command.");

        this.bentenganMiniGame = bentenganArena.getBentenganMiniGame();
        this.bentenganArena = bentenganArena;

        this.setPermission("holosports.bentengan.stop");
        this.setDescription("Start the match");
    }

    @Override
    protected void onCommand(final CommandSender sender, final BukkitCommandParameters parameters) throws CommandFailureException {
        if (bentenganArena.getState() != BentenganMatchState.WAITING) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThe match has already started!\n "));
            return;
        }

        if (bentenganMiniGame.getPlayerHelper().getPlayerList().size() < 2) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are not enough players! The minimum player size must be [2]\n "));
            return;
        }

        if (bentenganMiniGame.getTeamHelper().getTeamList().stream().anyMatch(team -> team.getPlayers().size() == 0)) {
            sender.sendMessage(CommonUtil.colorize("&c&lCan't start the match!"));
            sender.sendMessage(CommonUtil.colorize("&cThere are teams without players!\n "));

            sender.sendMessage(CommonUtil.colorize("&7The following teams don't have players:"));
            bentenganMiniGame.getTeamHelper().getTeamList().forEach(team -> {
                if (team.getPlayers().size() == 0) {
                    sender.sendMessage(CommonUtil.colorize("&8- &fTeam " + ChatColor.valueOf(team.getColor()) + team.getName()));
                }
            });
            return;
        }

        if (!bentenganArena.getBentenganMatchProperties().isStarting()) {
            bentenganArena.getBentenganMatchProperties().setStarting(true);
            bentenganArena.getBentenganMatchProperties().setStartingTaskId(Bukkit.getScheduler().runTaskTimer(bentenganMiniGame.getHoloSportsGame(), new StartingTask(bentenganArena), 0L, 20L).getTaskId());

            sender.sendMessage(ChatColor.GRAY + "[!] Starting match...");

            Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);

            return;
        }

        Bukkit.getScheduler().cancelTask(bentenganArena.getBentenganMatchProperties().getStartingTaskId());

        bentenganArena.getBentenganMatchProperties().resetTimer();

        bentenganArena.setState(BentenganMatchState.WAITING);

        sender.sendMessage(ChatColor.GRAY + "[!] Stopped match start.");
    }
}