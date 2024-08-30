package com.covercorp.holosports.game.minigame.potato;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.HoloSportsGame;

import com.covercorp.holosports.game.minigame.MiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import com.covercorp.holosports.game.minigame.potato.arena.state.PotatoMatchState;
import com.covercorp.holosports.game.minigame.potato.config.PotatoConfigHelper;
import com.covercorp.holosports.game.minigame.potato.listener.PlayerAccessListener;
import com.covercorp.holosports.game.minigame.potato.listener.PlayerStatsListener;
import com.covercorp.holosports.game.minigame.potato.npc.PotatoNpcHelper;
import com.covercorp.holosports.game.minigame.potato.player.IPotatoPlayerHelper;
import com.covercorp.holosports.game.minigame.potato.player.PotatoPlayerHelper;
import com.covercorp.holosports.game.minigame.potato.team.IPotatoTeamHelper;
import com.covercorp.holosports.game.minigame.potato.team.PotatoTeamHelper;
import com.covercorp.holosports.game.minigame.potato.ui.PotatoTeamChooseUI;
import com.covercorp.holosports.game.minigame.type.MiniGameType;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter(AccessLevel.PUBLIC)
public final class PotatoMiniGame extends MiniGame {
    private PotatoConfigHelper potatoConfigHelper;

    private IPotatoPlayerHelper playerHelper;
    private IPotatoTeamHelper teamHelper;

    private final PotatoArena arena;

    private final PotatoNpcHelper potatoNpcHelper;

    public PotatoMiniGame(final HoloSportsGame holoSportsGame) {
        super(holoSportsGame, MiniGameType.POTATO);

        setInventoryManager(new InventoryManager(holoSportsGame));

        potatoConfigHelper = new PotatoConfigHelper(
                getGameConfiguration()
        );

        playerHelper = new PotatoPlayerHelper(this);
        teamHelper = new PotatoTeamHelper(this);

        arena = new PotatoArena(this);

        potatoNpcHelper = new PotatoNpcHelper(this);

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

        potatoConfigHelper = null;
    }

    public void openTeamInventory(final Player player) {
        if (arena.getState() != PotatoMatchState.WAITING) {
            player.sendMessage(CommonUtil.colorize(" \n&c&lYou can't choose team right now."));
            player.sendMessage(CommonUtil.colorize("&cPlease wait until the actual game ends."));
            return;
        }

        final SmartInventory teamInventory = SmartInventory.builder()
                .id("TeamChooseUI")
                .manager(getHoloSportsGame().getMiniGame().getInventoryManager())
                .title(ChatColor.BLACK + "Choose a Team")
                .size(1, 9)
                .provider(new PotatoTeamChooseUI(this))
                .build();

        teamInventory.open(player);
    }
}