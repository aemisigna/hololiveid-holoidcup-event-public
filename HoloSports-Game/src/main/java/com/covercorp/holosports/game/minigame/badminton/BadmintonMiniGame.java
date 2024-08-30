package com.covercorp.holosports.game.minigame.badminton;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.MiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.config.BadmintonConfigHelper;
import com.covercorp.holosports.game.minigame.badminton.npc.BadmintonNpcHelper;
import com.covercorp.holosports.game.minigame.badminton.player.BadmintonPlayerHelper;
import com.covercorp.holosports.game.minigame.badminton.player.IBadmintonPlayerHelper;
import com.covercorp.holosports.game.minigame.badminton.team.BadmintonTeamHelper;
import com.covercorp.holosports.game.minigame.badminton.team.IBadmintonTeamHelper;
import com.covercorp.holosports.game.minigame.badminton.ui.BadmintonTeamChooseUI;
import com.covercorp.holosports.game.minigame.badminton.listener.PlayerAccessListener;
import com.covercorp.holosports.game.minigame.badminton.listener.PlayerStatsListener;
import com.covercorp.holosports.game.minigame.type.MiniGameType;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter(AccessLevel.PUBLIC)
public final class BadmintonMiniGame extends MiniGame {
    private BadmintonConfigHelper badmintonConfigHelper;

    private IBadmintonPlayerHelper playerHelper;
    private IBadmintonTeamHelper teamHelper;

    private final BadmintonArena arena;

    private final BadmintonNpcHelper badmintonNpcHelper;

    public BadmintonMiniGame(final HoloSportsGame holoSportsGame) {
        super(holoSportsGame, MiniGameType.BADMINTON);

        setInventoryManager(new InventoryManager(holoSportsGame));

        badmintonConfigHelper = new BadmintonConfigHelper(
                getGameConfiguration()
        );

        playerHelper = new BadmintonPlayerHelper(this);
        teamHelper = new BadmintonTeamHelper(this);

        arena = new BadmintonArena(this);

        badmintonNpcHelper = new BadmintonNpcHelper(this);

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

        badmintonConfigHelper = null;
    }

    public void openTeamInventory(final Player player) {
        if (arena.getState() != BadmintonMatchState.WAITING) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou can't choose team right now."));
            player.sendMessage(CommonUtil.colorize("&cPlease wait until the actual game ends."));
            return;
        }

        final SmartInventory teamInventory = SmartInventory.builder()
                .id("TeamChooseUI")
                .manager(getHoloSportsGame().getMiniGame().getInventoryManager())
                .title(ChatColor.BLACK + "Choose a Team")
                .size(1, 9)
                .provider(new BadmintonTeamChooseUI(this))
                .build();

        teamInventory.open(player);
    }
}