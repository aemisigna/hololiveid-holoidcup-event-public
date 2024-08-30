package com.covercorp.holosports.game.minigame.tug.npc.listener;

import com.covercorp.holosports.commons.util.BungeeUtil;
import com.covercorp.holosports.game.minigame.tug.npc.TugNpcHelper;
import com.covercorp.holosports.game.minigame.tug.npc.custom.CustomNpc;
import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    private final TugNpcHelper tugNpcHelper;

    public NpcListener(final TugNpcHelper tugNpcHelper) {
        this.tugNpcHelper = tugNpcHelper;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCInteract(NPC.Events.Interact event){
        final Player player = event.getPlayer();
        final NPC npc = event.getNPC();

        final CustomNpc customNpc = tugNpcHelper.getGameNpcMap().get(npc.getID().getSimpleID());

        if (customNpc == null) return;

        switch (customNpc.getClickType()) {
            case TEAM_SELECTOR -> {
                tugNpcHelper.getTugMiniGame().openTeamInventory(player);
            }
            case HUB -> {
                BungeeUtil.sendToServerButAwesome(tugNpcHelper.getTugMiniGame().getHoloSportsGame(), player, "MC-01");
            }
        }
    }
}
