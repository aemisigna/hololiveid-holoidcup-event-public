package com.covercorp.holosports.game.minigame.tug;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.MiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.TugArena;
import com.covercorp.holosports.game.minigame.tug.arena.state.TugMatchState;
import com.covercorp.holosports.game.minigame.tug.config.TugConfigHelper;
import com.covercorp.holosports.game.minigame.tug.listener.PlayerAccessListener;
import com.covercorp.holosports.game.minigame.tug.listener.PlayerStatsListener;
import com.covercorp.holosports.game.minigame.tug.npc.TugNpcHelper;
import com.covercorp.holosports.game.minigame.tug.player.ITugPlayerHelper;
import com.covercorp.holosports.game.minigame.tug.player.TugPlayerHelper;
import com.covercorp.holosports.game.minigame.tug.team.ITugTeamHelper;
import com.covercorp.holosports.game.minigame.tug.team.TugTeamHelper;
import com.covercorp.holosports.game.minigame.tug.ui.TugTeamChooseUI;
import com.covercorp.holosports.game.minigame.type.MiniGameType;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter(AccessLevel.PUBLIC)
public final class TugMiniGame extends MiniGame {
    private TugConfigHelper tugConfigHelper;

    private ITugPlayerHelper playerHelper;
    private ITugTeamHelper teamHelper;

    private final TugArena arena;

    private final TugNpcHelper tugNpcHelper;

    public TugMiniGame(final HoloSportsGame holoSportsGame) {
        super(holoSportsGame, MiniGameType.TUG);

        setInventoryManager(new InventoryManager(holoSportsGame));

        tugConfigHelper = new TugConfigHelper(
                getGameConfiguration()
        );

        playerHelper = new TugPlayerHelper(this);
        teamHelper = new TugTeamHelper(this);

        arena = new TugArena(this);

        tugNpcHelper = new TugNpcHelper(this);

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

        tugConfigHelper = null;
    }

    public void openTeamInventory(final Player player) {
        if (arena.getState() != TugMatchState.WAITING) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou can't choose team right now."));
            player.sendMessage(CommonUtil.colorize("&cPlease wait until the actual game ends."));
            return;
        }

        final SmartInventory teamInventory = SmartInventory.builder()
                .id("TeamChooseUI")
                .manager(getHoloSportsGame().getMiniGame().getInventoryManager())
                .title(ChatColor.BLACK + "Choose a Team")
                .size(1, 9)
                .provider(new TugTeamChooseUI(this))
                .build();

        teamInventory.open(player);
    }
}