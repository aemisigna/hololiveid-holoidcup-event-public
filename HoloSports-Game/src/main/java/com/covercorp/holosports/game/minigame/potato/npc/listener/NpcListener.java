package com.covercorp.holosports.game.minigame.potato.npc.listener;

import com.covercorp.holosports.commons.util.BungeeUtil;
import com.covercorp.holosports.game.minigame.potato.npc.PotatoNpcHelper;
import com.covercorp.holosports.game.minigame.potato.npc.custom.CustomNpc;
import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    private final PotatoNpcHelper potatoNpcHelper;

    public NpcListener(final PotatoNpcHelper potatoNpcHelper) {
        this.potatoNpcHelper = potatoNpcHelper;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCInteract(NPC.Events.Interact event){
        final Player player = event.getPlayer();
        final NPC npc = event.getNPC();

        final CustomNpc customNpc = potatoNpcHelper.getGameNpcMap().get(npc.getID().getSimpleID());

        if (customNpc == null) return;

        switch (customNpc.getClickType()) {
            case TEAM_SELECTOR -> {
                potatoNpcHelper.getPotatoMiniGame().openTeamInventory(player);
            }
            case HUB -> {
                BungeeUtil.sendToServerButAwesome(potatoNpcHelper.getPotatoMiniGame().getHoloSportsGame(), player, "MC-01");
            }
        }
    }
}
