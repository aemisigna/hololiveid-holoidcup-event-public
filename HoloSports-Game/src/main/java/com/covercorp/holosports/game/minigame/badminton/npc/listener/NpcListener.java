package com.covercorp.holosports.game.minigame.badminton.npc.listener;

import com.covercorp.holosports.commons.util.BungeeUtil;
import com.covercorp.holosports.game.minigame.badminton.npc.BadmintonNpcHelper;
import com.covercorp.holosports.game.minigame.badminton.npc.custom.CustomNpc;

import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    private final BadmintonNpcHelper badmintonNpcHelper;

    public NpcListener(final BadmintonNpcHelper badmintonNpcHelper) {
        this.badmintonNpcHelper = badmintonNpcHelper;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCInteract(NPC.Events.Interact event){
        final Player player = event.getPlayer();
        final NPC npc = event.getNPC();

        final CustomNpc customNpc = badmintonNpcHelper.getGameNpcMap().get(npc.getID().getSimpleID());

        if (customNpc == null) return;

        switch (customNpc.getClickType()) {
            case TEAM_SELECTOR -> {
                badmintonNpcHelper.getBadmintonMiniGame().openTeamInventory(player);
            }
            case HUB -> {
                BungeeUtil.sendToServerButAwesome(badmintonNpcHelper.getBadmintonMiniGame().getHoloSportsGame(), player, "MC-01");
            }
        }
    }
}
