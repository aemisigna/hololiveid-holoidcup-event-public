package com.covercorp.holosports.game.minigame.potato.arena.inventory;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class PotatoGameItemCollection {
    private final static String HOTBAR_ERROR_NULL_PLAYER = ChatColor.RED + "Could not setup game hotbar for %s";
    private final static String HOTBAR_ERROR = ChatColor.RED + "Could not setup your game hotbar.";
    private final static String HOTBAR_ERROR_TEAM = ChatColor.RED + "Can't setup your hotbar, you are not in a team!";

    public static void setupPlayerHotbar(final IPotatoPlayer potatoPlayer) {
        final Player player = Bukkit.getPlayer(potatoPlayer.getUniqueId());

        if (player == null) {
            HoloSportsGame.getHoloSportsGame().getLogger().severe(String.format(HOTBAR_ERROR_NULL_PLAYER, potatoPlayer.getUniqueId()));
            return;
        }

        final IPotatoTeam potatoTeam = potatoPlayer.getTeam();
        if (potatoTeam == null) {
            player.sendMessage(CommonUtil.colorize(HOTBAR_ERROR_TEAM));
            return;
        }

        final Inventory inventory = player.getInventory();

        final ChatColor color = ChatColor.valueOf(potatoTeam.getIdentifier().toUpperCase());
        final ItemStack[] armor = new ItemStack[] {
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.LEATHER_HELMET, 1)
        };

        for (final ItemStack itemStack : armor) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            final Color armorColor = Color.fromRGB(0, 0, 0);

            if (meta == null) continue;

            NBTMetadataUtil.addString(itemStack, "accessor", "game_armor");

            try {
                //if (potatoPlayer.isReferee()) {
                    //meta.setColor(Color.BLACK);
                //} else {
                    meta.setColor((Color) armorColor.getClass().getDeclaredField(color.name()).get(armorColor));
                //}
            } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            itemStack.setItemMeta(meta);
        }

        player.getInventory().setArmorContents(armor);

        inventory.setItem(5, PotatoGameItemCollection.getStartItem());
        inventory.setItem(6, PotatoGameItemCollection.getStopItem());
    }

    public static void resetPlayerHotbar(final IPotatoPlayer potatoPlayer) {
        final Player player = Bukkit.getPlayer(potatoPlayer.getUniqueId());

        if (player == null) {
            HoloSportsGame.getHoloSportsGame().getLogger().severe(String.format(HOTBAR_ERROR_NULL_PLAYER, potatoPlayer.getUniqueId()));
            return;
        }

        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        final Inventory inventory = player.getInventory();

        inventory.clear();
    }

    public static ItemStack getStartItem() {
        return new ItemBuilder(Material.BLAZE_POWDER)
                .withAmount(1)
                .withName("&aStart game &7(Click to use)")
                .withCustomModelData(1000)
                .withNBTTag("accessor", "start_game")
                .build();
    }

    public static ItemStack getStopItem() {
        return new ItemBuilder(Material.BARRIER)
                .withAmount(1)
                .withName("&cStop game &7(Click to use)")
                .withCustomModelData(1000)
                .withNBTTag("accessor", "stop_game")
                .build();
    }
}
