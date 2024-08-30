package com.covercorp.holosports.game.minigame.soccer.npc;

import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.npc.custom.ClickType;
import com.covercorp.holosports.game.minigame.soccer.npc.custom.CustomNpc;
import com.covercorp.holosports.game.minigame.soccer.npc.listener.NpcListener;

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

public final class SoccerNpcHelper {
    @Getter(AccessLevel.PUBLIC) private final SoccerMiniGame soccerMiniGame;

    @Getter(AccessLevel.PUBLIC) private final Map<String, CustomNpc> gameNpcMap;

    public SoccerNpcHelper(final SoccerMiniGame soccerMiniGame) {
        this.soccerMiniGame = soccerMiniGame;

        NPCLib.getInstance().registerPlugin(soccerMiniGame.getHoloSportsGame());

        gameNpcMap = new ConcurrentHashMap<>();

        final Location teamChooseLocation = soccerMiniGame.getSoccerConfigHelper().getTeamChooseNpcLocation();
        final NPC.Global teamChooseNpc = NPCLib.getInstance().generateGlobalNPC(soccerMiniGame.getHoloSportsGame(), "team-choose", teamChooseLocation);

        final String[] MOONA_SKIN = new String[]{
                "ewogICJ0aW1lc3RhbXAiIDogMTY4NjU1MzQyOTkxOSwKICAicHJvZmlsZUlkIiA6ICJiMDU4MTFjYTdmNDk0YTM5OTZiNDU4ZjcwMmQ2MzJiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJVeWlsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExZDkwMmM1MmJjZDNmNTRmYmExYWRmMjZjNDUzYThkYjc0MjhjOTk5NjI0ODhjZGYyZGJkZTJmZWRlODQxYzYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "PHe0EOkU/Cqq1hYaWLsT0924rM895MIzAF9DdbpyHscSLkgjT0xRKr9PINBFfGx9b/IF8/9DF37SSJjmsIv2r8y12Bgtzez2eI88MDhRcntOAjKmzsczG13kgfjZElasxvLCVCbpd391YSBzRbBM8b7GmmjjHcjVcrf6yhoyJv5VI49FuqnhUFsrgX6F+ggT8ctTkzo+fxvN6vEvoPUwdOhI2QY/AvQycuSW3E1I4z3vXvY49n7RokclHS3YBJoNGEZ2ui5119f8WffJAP/jMrw7A7ZaFA3JhdGjLeOhvAy8bXE7CureLIzgv4B3QCVfZ4gjypKdtskWcVX4eji34pgM1iAI3mGMDxTmmNDUJH3eEpzYUvKhuIKxyk0mlmug8nr5EpcUm5vKsTpugrW+3EWl92GFuYTOmRSXmJFIrP/yMTnDVlh08JbUUgYeFwZRIIz9cmBfdeSyQ5/HOdEiziJ+vk9R8DqZTw4EKSnTu9TJ8mcpXSIgBIDMgxyacacoaw31P7LnfgLVbWqOtTL9fusjG+Q/er3Y8cI1Uwu8mi6FPcO5d/90K8SLIlczT0Qq4/pykWAGQ4laKvmFV9cO8LRTkKDME6Y3JMigUFOGmLJ0Hj+0ti9gT5yfSLq4lpkuE10Mr2LxpkGP3yek2ceW4P8eVdJqXhRs35bzqqeAsng="
        };

        teamChooseNpc.setSkin(MOONA_SKIN[0], MOONA_SKIN[1]);
        teamChooseNpc.setItemInMainHand(new ItemStack(Material.LIME_WOOL));
        teamChooseNpc.setGlowing(false, ChatColor.YELLOW);
        teamChooseNpc.setShowNameTag(false);
        teamChooseNpc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

        teamChooseNpc.setText(ChatColor.RESET + "\uE250 " + ChatColor.AQUA + "Team Selector", ChatColor.YELLOW + "Click to interact");

        teamChooseNpc.update();

        gameNpcMap.put("global_" + teamChooseNpc.getSimpleID(), new CustomNpc(teamChooseNpc, ClickType.TEAM_SELECTOR));

        final Location roleChooseLocation = soccerMiniGame.getSoccerConfigHelper().getRoleChooseNpcLocation();
        final NPC.Global roleChooseNpc = NPCLib.getInstance().generateGlobalNPC(soccerMiniGame.getHoloSportsGame(), "role-choose", roleChooseLocation);

        final String[] KAELA_SKIN = new String[]{
                "ewogICJ0aW1lc3RhbXAiIDogMTY4MjQ4MzgyNjQxOSwKICAicHJvZmlsZUlkIiA6ICJiMDU4MTFjYTdmNDk0YTM5OTZiNDU4ZjcwMmQ2MzJiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJVeWlsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzYyMDAwYzExOTA3MGQ5ZDE5ZTBiMTExZDc5Y2QwOGE1OGEwZjY3NGRkNjc4M2ZjZDI0MWM5ZjM1YjJkMjE4YTciLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "uijRZ3G7XQtX/9mQJTxzzN0HiSxjW1ZGKuwJ2hxl5+iDW/WjV93KETqQwv1QSjZar0tbk+y/farDWBJeiyHWt9CFefehTzuWxL7BaE7tzc/QjAZeq0H5tVgEe38xPcyIZoO0564jevCvgyh7eAywUnUD6NoyNs0Kah0g1P48Qwbth8NvIDi2RK0SSO+0rBKOdxCNYKSk2BMlDZM3Ojjgqa+Rr6ysb2r0pVO6RmgFQmArRo393dIB3LhXbwOcBwlfbQF29AS6khUqTKOvTH6AXLQP1T2r5dM8b85PIFBUAlXK+B7Ezw47qMhOA+voMP2scuy7HcTfOot3rJ7j9VTSDRoUjnjgd7SqysTJGdH1YfH8lOvqVklmqzu3gek/goqhEC+U+D/TKtbSx2cgrBWX0y2i3N+C+YlpLJtHGIVw0d2St2KfAlw7BmZSNpfUnXZo3aTIGoSUksHa97snvwkhKtIe0TWCrJ/loWuFgxUqqb9/JkVq3uMtdDsP7qlV2AlaCkaX12g1t5QOmP8ovx4UYY+OQGO8XVzzxujCn6JnXBlYCsddUCyQ+LhTrmjUCs0Pm4D0Usjt8y5wC6e+m808t1APFdHZJZkUYv3pN3BcfCDsrx4cZdVQtmRqjekJz2R4NdckyT0hycztDFU71rrk+J04RrXhJyvyDcojIaPcB8E="
        };

        roleChooseNpc.setSkin(KAELA_SKIN[0], KAELA_SKIN[1]);
        roleChooseNpc.setItemInMainHand(SoccerGameItemCollection.getKickItem());
        roleChooseNpc.setItemInOffHand(SoccerGameItemCollection.getHoldItem());
        roleChooseNpc.setGlowing(false, ChatColor.BLUE);
        roleChooseNpc.setShowNameTag(false);
        roleChooseNpc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

        roleChooseNpc.setText(ChatColor.RESET + "\uE250 " + ChatColor.AQUA + "Role Selector", ChatColor.YELLOW + "Click to interact");

        roleChooseNpc.update();

        gameNpcMap.put("global_" + roleChooseNpc.getSimpleID(), new CustomNpc(roleChooseNpc, ClickType.ROLE_SELECTOR));

        final Location hubChooseLocation = soccerMiniGame.getSoccerConfigHelper().getHubNpcLocation();
        final NPC.Global hubChooseNpc = NPCLib.getInstance().generateGlobalNPC(soccerMiniGame.getHoloSportsGame(), "hub-portal", hubChooseLocation);

        hubChooseNpc.setSkin(MOONA_SKIN[0], MOONA_SKIN[1]);
        hubChooseNpc.setItemInMainHand(new ItemStack(Material.END_PORTAL_FRAME));
        hubChooseNpc.setGlowing(false, ChatColor.AQUA);
        hubChooseNpc.setShowNameTag(false);
        hubChooseNpc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

        hubChooseNpc.setText(ChatColor.RESET + "\uE250 " + ChatColor.AQUA + "Main Hub", ChatColor.YELLOW + "Click to go back!");

        hubChooseNpc.update();

        gameNpcMap.put("global_" + hubChooseNpc.getSimpleID(), new CustomNpc(hubChooseNpc, ClickType.HUB));

        Bukkit.getServer().getPluginManager().registerEvents(new NpcListener(this), soccerMiniGame.getHoloSportsGame());
    }
}
