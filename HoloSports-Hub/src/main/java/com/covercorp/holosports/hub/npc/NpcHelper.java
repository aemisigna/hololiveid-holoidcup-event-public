package com.covercorp.holosports.hub.npc;

import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.hub.HoloSportsHub;
import com.covercorp.holosports.hub.npc.custom.CustomNpc;
import com.covercorp.holosports.hub.npc.listener.NpcListener;

import dev.sergiferry.playernpc.api.NPC;

import dev.sergiferry.playernpc.api.NPCLib;
import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public final class NpcHelper {
    @Getter(AccessLevel.PUBLIC) private final HoloSportsHub holoSportsHub;

    @Getter(AccessLevel.PUBLIC) private final Map<String, CustomNpc> gameNpcMap;

    private final String[] DOREMY_SKIN = new String[] {
            "ewogICJ0aW1lc3RhbXAiIDogMTY4NjU1MzQyOTkxOSwKICAicHJvZmlsZUlkIiA6ICJiMDU4MTFjYTdmNDk0YTM5OTZiNDU4ZjcwMmQ2MzJiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJVeWlsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExZDkwMmM1MmJjZDNmNTRmYmExYWRmMjZjNDUzYThkYjc0MjhjOTk5NjI0ODhjZGYyZGJkZTJmZWRlODQxYzYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "PHe0EOkU/Cqq1hYaWLsT0924rM895MIzAF9DdbpyHscSLkgjT0xRKr9PINBFfGx9b/IF8/9DF37SSJjmsIv2r8y12Bgtzez2eI88MDhRcntOAjKmzsczG13kgfjZElasxvLCVCbpd391YSBzRbBM8b7GmmjjHcjVcrf6yhoyJv5VI49FuqnhUFsrgX6F+ggT8ctTkzo+fxvN6vEvoPUwdOhI2QY/AvQycuSW3E1I4z3vXvY49n7RokclHS3YBJoNGEZ2ui5119f8WffJAP/jMrw7A7ZaFA3JhdGjLeOhvAy8bXE7CureLIzgv4B3QCVfZ4gjypKdtskWcVX4eji34pgM1iAI3mGMDxTmmNDUJH3eEpzYUvKhuIKxyk0mlmug8nr5EpcUm5vKsTpugrW+3EWl92GFuYTOmRSXmJFIrP/yMTnDVlh08JbUUgYeFwZRIIz9cmBfdeSyQ5/HOdEiziJ+vk9R8DqZTw4EKSnTu9TJ8mcpXSIgBIDMgxyacacoaw31P7LnfgLVbWqOtTL9fusjG+Q/er3Y8cI1Uwu8mi6FPcO5d/90K8SLIlczT0Qq4/pykWAGQ4laKvmFV9cO8LRTkKDME6Y3JMigUFOGmLJ0Hj+0ti9gT5yfSLq4lpkuE10Mr2LxpkGP3yek2ceW4P8eVdJqXhRs35bzqqeAsng="
    };

    public NpcHelper(final HoloSportsHub holoSportsHub) {
        this.holoSportsHub = holoSportsHub;

        NPCLib.getInstance().registerPlugin(holoSportsHub);

        gameNpcMap = new HashMap<>();

        holoSportsHub.getConfigHelper().getGameNpcConfigs().forEach(config -> {
            final NPC.Global npc = NPCLib.getInstance().generateGlobalNPC(holoSportsHub, config.getServerId(), config.getNpcLocation());

            npc.setSkin(DOREMY_SKIN[0], DOREMY_SKIN[1]);
            final ItemBuilder item = new ItemBuilder(config.getItem());
            if (config.getModel() != 0) item.withCustomModelData(config.getModel());

            npc.setItemInMainHand(item.build());
            npc.setGlowing(false, config.getColor());
            npc.setShowNameTag(false);
            npc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

            npc.setText(ChatColor.RESET + "\uE250 " + ChatColor.AQUA + config.getDisplay(), ChatColor.YELLOW + "Click to play");

            npc.update();

            gameNpcMap.put("global_" + npc.getSimpleID(), new CustomNpc(npc, config.getDisplay(), config.getServerId()));
        });

        holoSportsHub.getServer().getPluginManager().registerEvents(new NpcListener(this), holoSportsHub);
    }
}
