package com.covercorp.holosports.game.minigame.bentengan.npc.listener;

import com.covercorp.holosports.commons.util.BungeeUtil;
import com.covercorp.holosports.game.minigame.bentengan.npc.BentenganNpcHelper;
import com.covercorp.holosports.game.minigame.bentengan.npc.custom.CustomNpc;
import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    private final BentenganNpcHelper bentenganNpcHelper;

    public NpcListener(final BentenganNpcHelper bentenganNpcHelper) {
        this.bentenganNpcHelper = bentenganNpcHelper;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCInteract(NPC.Events.Interact event){
        final Player player = event.getPlayer();
        final NPC npc = event.getNPC();

        final CustomNpc customNpc = bentenganNpcHelper.getGameNpcMap().get(npc.getID().getSimpleID());

        if (customNpc == null) return;

        switch (customNpc.getClickType()) {
            case TEAM_SELECTOR -> {
                bentenganNpcHelper.getBentenganMiniGame().openTeamInventory(player);
            }
            case HUB -> {
                BungeeUtil.sendToServerButAwesome(bentenganNpcHelper.getBentenganMiniGame().getHoloSportsGame(), player, "MC-01");
            }
        }
    }
}
