package com.covercorp.holosports.game.minigame.soccer.npc.listener;

import com.covercorp.holosports.commons.util.BungeeUtil;
import com.covercorp.holosports.game.minigame.soccer.npc.SoccerNpcHelper;
import com.covercorp.holosports.game.minigame.soccer.npc.custom.CustomNpc;

import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    private final SoccerNpcHelper soccerNpcHelper;

    public NpcListener(final SoccerNpcHelper soccerNpcHelper) {
        this.soccerNpcHelper = soccerNpcHelper;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCInteract(NPC.Events.Interact event){
        final Player player = event.getPlayer();
        final NPC npc = event.getNPC();

        final CustomNpc customNpc = soccerNpcHelper.getGameNpcMap().get(npc.getID().getSimpleID());

        if (customNpc == null) return;

        switch (customNpc.getClickType()) {
            case ROLE_SELECTOR -> {
                soccerNpcHelper.getSoccerMiniGame().openRoleInventory(player);
            }
            case TEAM_SELECTOR -> {
                soccerNpcHelper.getSoccerMiniGame().openTeamInventory(player);
            }
            case HUB -> {
                BungeeUtil.sendToServerButAwesome(soccerNpcHelper.getSoccerMiniGame().getHoloSportsGame(), player, "MC-01");
            }
        }
    }
}
