package com.covercorp.holosports.game.minigame.bentengan;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.MiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import com.covercorp.holosports.game.minigame.bentengan.arena.state.BentenganMatchState;
import com.covercorp.holosports.game.minigame.bentengan.config.BentenganConfigHelper;
import com.covercorp.holosports.game.minigame.bentengan.listener.PlayerAccessListener;
import com.covercorp.holosports.game.minigame.bentengan.listener.PlayerStatsListener;
import com.covercorp.holosports.game.minigame.bentengan.npc.BentenganNpcHelper;
import com.covercorp.holosports.game.minigame.bentengan.player.BentenganPlayerHelper;
import com.covercorp.holosports.game.minigame.bentengan.player.IBentenganPlayerHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.BentenganTeamHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.IBentenganTeamHelper;
import com.covercorp.holosports.game.minigame.bentengan.ui.BentenganTeamChooseUI;
import com.covercorp.holosports.game.minigame.type.MiniGameType;

import fr.minuskube.inv.InventoryManager;

import fr.minuskube.inv.SmartInventory;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter(AccessLevel.PUBLIC)
public final class BentenganMiniGame extends MiniGame {
    private BentenganConfigHelper bentenganConfigHelper;

    private IBentenganPlayerHelper playerHelper;
    private IBentenganTeamHelper teamHelper;

    private final BentenganArena arena;

    private final BentenganNpcHelper bentenganNpcHelper;

    public BentenganMiniGame(final HoloSportsGame holoSportsGame) {
        super(holoSportsGame, MiniGameType.BENTENGAN);

        setInventoryManager(new InventoryManager(holoSportsGame));

        bentenganConfigHelper = new BentenganConfigHelper(
                getGameConfiguration()
        );

        playerHelper = new BentenganPlayerHelper(this);
        teamHelper = new BentenganTeamHelper(this);

        teamHelper.registerTeams();

        arena = new BentenganArena(this);

        bentenganNpcHelper = new BentenganNpcHelper(this);

        holoSportsGame.getServer().getPluginManager().registerEvents(new PlayerStatsListener(this), holoSportsGame);
        holoSportsGame.getServer().getPluginManager().registerEvents(new PlayerAccessListener(this), holoSportsGame);
    }

    @Override
    public void onGameLoad() {
        getInventoryManager().init();
    }

    @Override
    public void onGameUnload() {
        playerHelper.clearPlayerList();
        teamHelper.unregisterTeams();

        setInventoryManager(null);

        playerHelper = null;
        teamHelper = null;

        bentenganConfigHelper = null;
    }

    public void openTeamInventory(final Player player) {
        if (arena.getState() != BentenganMatchState.WAITING) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou can't choose team right now."));
            player.sendMessage(CommonUtil.colorize("&cPlease wait until the actual game ends."));
            return;
        }

        final SmartInventory teamInventory = SmartInventory.builder()
                .id("TeamChooseUI")
                .manager(getHoloSportsGame().getMiniGame().getInventoryManager())
                .title(ChatColor.BLACK + "Choose a Team")
                .size(1, 9)
                .provider(new BentenganTeamChooseUI(this))
                .build();

        teamInventory.open(player);
    }
}