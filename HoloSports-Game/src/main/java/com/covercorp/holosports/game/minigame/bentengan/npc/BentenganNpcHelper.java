package com.covercorp.holosports.game.minigame.bentengan.npc;

import com.covercorp.holosports.game.minigame.bentengan.npc.custom.ClickType;
import com.covercorp.holosports.game.minigame.bentengan.npc.custom.CustomNpc;
import com.covercorp.holosports.game.minigame.bentengan.npc.listener.NpcListener;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BentenganNpcHelper {
    @Getter(AccessLevel.PUBLIC) private final BentenganMiniGame bentenganMiniGame;

    @Getter(AccessLevel.PUBLIC) private final Map<String, CustomNpc> gameNpcMap;

    public BentenganNpcHelper(final BentenganMiniGame bentenganMiniGame) {
        this.bentenganMiniGame = bentenganMiniGame;

        NPCLib.getInstance().registerPlugin(bentenganMiniGame.getHoloSportsGame());

        gameNpcMap = new ConcurrentHashMap<>();

        final Location teamChooseLocation = bentenganMiniGame.getBentenganConfigHelper().getTeamChooseNpcLocation();
        final NPC.Global teamChooseNpc = NPCLib.getInstance().generateGlobalNPC(bentenganMiniGame.getHoloSportsGame(), "team-choose", teamChooseLocation);

        final String[] DOREMY_SKIN = new String[]{
                "ewogICJ0aW1lc3RhbXAiIDogMTY4NjU1MzQyOTkxOSwKICAicHJvZmlsZUlkIiA6ICJiMDU4MTFjYTdmNDk0YTM5OTZiNDU4ZjcwMmQ2MzJiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJVeWlsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExZDkwMmM1MmJjZDNmNTRmYmExYWRmMjZjNDUzYThkYjc0MjhjOTk5NjI0ODhjZGYyZGJkZTJmZWRlODQxYzYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "PHe0EOkU/Cqq1hYaWLsT0924rM895MIzAF9DdbpyHscSLkgjT0xRKr9PINBFfGx9b/IF8/9DF37SSJjmsIv2r8y12Bgtzez2eI88MDhRcntOAjKmzsczG13kgfjZElasxvLCVCbpd391YSBzRbBM8b7GmmjjHcjVcrf6yhoyJv5VI49FuqnhUFsrgX6F+ggT8ctTkzo+fxvN6vEvoPUwdOhI2QY/AvQycuSW3E1I4z3vXvY49n7RokclHS3YBJoNGEZ2ui5119f8WffJAP/jMrw7A7ZaFA3JhdGjLeOhvAy8bXE7CureLIzgv4B3QCVfZ4gjypKdtskWcVX4eji34pgM1iAI3mGMDxTmmNDUJH3eEpzYUvKhuIKxyk0mlmug8nr5EpcUm5vKsTpugrW+3EWl92GFuYTOmRSXmJFIrP/yMTnDVlh08JbUUgYeFwZRIIz9cmBfdeSyQ5/HOdEiziJ+vk9R8DqZTw4EKSnTu9TJ8mcpXSIgBIDMgxyacacoaw31P7LnfgLVbWqOtTL9fusjG+Q/er3Y8cI1Uwu8mi6FPcO5d/90K8SLIlczT0Qq4/pykWAGQ4laKvmFV9cO8LRTkKDME6Y3JMigUFOGmLJ0Hj+0ti9gT5yfSLq4lpkuE10Mr2LxpkGP3yek2ceW4P8eVdJqXhRs35bzqqeAsng="
        };

        teamChooseNpc.setSkin(DOREMY_SKIN[0], DOREMY_SKIN[1]);
        teamChooseNpc.setItemInMainHand(new ItemStack(Material.LIME_WOOL));
        teamChooseNpc.setGlowing(false, ChatColor.YELLOW);
        teamChooseNpc.setShowNameTag(false);
        teamChooseNpc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

        teamChooseNpc.setText(ChatColor.RESET + "\uE250 " + ChatColor.AQUA + "Team Selector", ChatColor.YELLOW + "Click to interact");

        teamChooseNpc.update();

        gameNpcMap.put("global_" + teamChooseNpc.getSimpleID(), new CustomNpc(teamChooseNpc, ClickType.TEAM_SELECTOR));

        final Location hubChooseLocation = bentenganMiniGame.getBentenganConfigHelper().getHubNpcLocation();
        final NPC.Global hubChooseNpc = NPCLib.getInstance().generateGlobalNPC(bentenganMiniGame.getHoloSportsGame(), "hub-portal", hubChooseLocation);

        hubChooseNpc.setSkin(DOREMY_SKIN[0], DOREMY_SKIN[1]);
        hubChooseNpc.setItemInMainHand(new ItemStack(Material.END_PORTAL_FRAME));
        hubChooseNpc.setGlowing(false, ChatColor.AQUA);
        hubChooseNpc.setShowNameTag(false);
        hubChooseNpc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

        hubChooseNpc.setText(ChatColor.RESET + "\uE250 " + ChatColor.AQUA + "Main Hub", ChatColor.YELLOW + "Click to go back!");

        hubChooseNpc.update();

        gameNpcMap.put("global_" + hubChooseNpc.getSimpleID(), new CustomNpc(hubChooseNpc, ClickType.HUB));

        Bukkit.getServer().getPluginManager().registerEvents(new NpcListener(this), bentenganMiniGame.getHoloSportsGame());
    }
}
