package com.covercorp.holosports.game.minigame.soccer;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.MiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.config.SoccerConfigHelper;
import com.covercorp.holosports.game.minigame.soccer.listener.BallListener;
import com.covercorp.holosports.game.minigame.soccer.listener.MatchListener;
import com.covercorp.holosports.game.minigame.soccer.listener.PlayerAccessListener;
import com.covercorp.holosports.game.minigame.soccer.listener.PlayerStatsListener;
import com.covercorp.holosports.game.minigame.soccer.npc.SoccerNpcHelper;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.player.SoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.arena.state.SoccerMatchState;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.SoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import com.covercorp.holosports.game.minigame.soccer.ui.RoleChooseUI;
import com.covercorp.holosports.game.minigame.soccer.ui.SoccerTeamChooseUI;
import com.covercorp.holosports.game.minigame.type.MiniGameType;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

@Getter(AccessLevel.PUBLIC)
public final class SoccerMiniGame extends MiniGame {
    private SoccerConfigHelper soccerConfigHelper;

    private ISoccerPlayerHelper playerHelper;
    private ISoccerTeamHelper teamHelper;

    private final SoccerArena arena;

    private final SoccerNpcHelper soccerNpcHelper;

    public SoccerMiniGame(final HoloSportsGame holoSportsGame) {
        super(holoSportsGame, MiniGameType.SOCCER);

        setInventoryManager(new InventoryManager(holoSportsGame));

        soccerConfigHelper = new SoccerConfigHelper(
                getGameConfiguration()
        );

        playerHelper = new SoccerPlayerHelper(this);
        teamHelper = new SoccerTeamHelper(this);

        arena = new SoccerArena(this);

        soccerNpcHelper = new SoccerNpcHelper(this);

        holoSportsGame.getServer().getPluginManager().registerEvents(new MatchListener(this), holoSportsGame);
        holoSportsGame.getServer().getPluginManager().registerEvents(new BallListener(this), holoSportsGame);
        holoSportsGame.getServer().getPluginManager().registerEvents(new PlayerStatsListener(this), holoSportsGame);
        holoSportsGame.getServer().getPluginManager().registerEvents(new PlayerAccessListener(this), holoSportsGame);
    }

    @Override
    public void onGameLoad() {
        getInventoryManager().init();

        teamHelper.registerTeams();
    }

    @Override
    public void onGameUnload() {
        playerHelper.clearPlayerList();
        teamHelper.unregisterTeams();

        setInventoryManager(null);

        playerHelper = null;
        teamHelper = null;
        soccerConfigHelper = null;
    }

    public void openTeamInventory(final Player player) {
        if (arena.getState() != SoccerMatchState.WAITING) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou can't choose team right now."));
            player.sendMessage(CommonUtil.colorize("&cPlease wait until the actual game ends."));
            return;
        }

        final SmartInventory teamInventory = SmartInventory.builder()
                .id("TeamChooseUI")
                .manager(getHoloSportsGame().getMiniGame().getInventoryManager())
                .title(ChatColor.BLACK + "Choose a Team")
                .size(1, 9)
                .provider(new SoccerTeamChooseUI(this))
                .build();

        teamInventory.open(player);
    }

    public void openRoleInventory(final Player player) {
        if (arena.getState() != SoccerMatchState.WAITING) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou can't choose a role right now."));
            player.sendMessage(CommonUtil.colorize("&cPlease wait until the actual game ends."));
            return;
        }

        final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());

        if (soccerPlayerOptional.isEmpty()) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou are not participating in the next game."));
            player.sendMessage(CommonUtil.colorize("&cPlease, choose a team to participate first. [1]"));
            return;
        }

        final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
        final ISoccerTeam team = soccerPlayer.getTeam();

        if (team == null) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou are not participating in the next game."));
            player.sendMessage(CommonUtil.colorize("&cPlease, choose a team to participate first. [2]"));
            return;
        }

        final SmartInventory roleChooseInventory = SmartInventory.builder()
                .id("RoleChooseUI")
                .manager(getHoloSportsGame().getMiniGame().getInventoryManager())
                .title(ChatColor.BLACK + "Choose a Role")
                .size(1, 9)
                .provider(new RoleChooseUI(this, soccerPlayer))
                .build();

        roleChooseInventory.open(player);
    }
}