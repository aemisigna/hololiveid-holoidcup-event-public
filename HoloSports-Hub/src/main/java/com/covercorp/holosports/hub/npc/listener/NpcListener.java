package com.covercorp.holosports.hub.npc.listener;

import com.covercorp.holosports.commons.util.BungeeUtil;
import com.covercorp.holosports.hub.npc.NpcHelper;
import com.covercorp.holosports.hub.npc.custom.CustomNpc;

import dev.sergiferry.playernpc.api.NPC;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    private final NpcHelper npcHelper;
    public NpcListener(final NpcHelper npcHelper) {
        this.npcHelper = npcHelper;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCInteract(NPC.Events.Interact event){
        final Player player = event.getPlayer();
        final NPC npc = event.getNPC();

        final CustomNpc customNpc = npcHelper.getGameNpcMap().get(npc.getID().getSimpleID());

        if (customNpc == null) return;

        BungeeUtil.sendToServerButAwesome(npcHelper.getHoloSportsHub(), player, customNpc.getServerId());
    }
}
