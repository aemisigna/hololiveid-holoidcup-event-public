package com.covercorp.holosports.game.minigame.badminton.arena.inventory;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;

import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class BadmintonGameItemCollection {
    private final static String HOTBAR_ERROR_NULL_PLAYER = ChatColor.RED + "Could not setup game hotbar for %s";
    private final static String HOTBAR_ERROR_TEAM = ChatColor.RED + "Can't setup your hotbar, you are not in a team!";

    public static void setupPlayerHotbar(final IBadmintonPlayer badmintonPlayer) {
        final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());

        if (player == null) {
            HoloSportsGame.getHoloSportsGame().getLogger().severe(String.format(HOTBAR_ERROR_NULL_PLAYER, badmintonPlayer.getUniqueId()));
            return;
        }

        final IBadmintonTeam badmintonTeam = badmintonPlayer.getTeam();
        if (badmintonTeam == null) {
            player.sendMessage(CommonUtil.colorize(HOTBAR_ERROR_TEAM));
            return;
        }

        final Inventory inventory = player.getInventory();

        final ChatColor color = ChatColor.valueOf(badmintonTeam.getIdentifier().toUpperCase());
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
                //if (badmintonPlayer.isReferee()) {
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

        inventory.setItem(0, getRacketItem());

        inventory.setItem(5, BadmintonGameItemCollection.getStartItem());
        inventory.setItem(6, BadmintonGameItemCollection.getStopItem());
    }

    public static void resetPlayerHotbar(final IBadmintonPlayer badmintonPlayer) {
        final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());

        if (player == null) {
            HoloSportsGame.getHoloSportsGame().getLogger().severe(String.format(HOTBAR_ERROR_NULL_PLAYER, badmintonPlayer.getUniqueId()));
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

    public static ItemStack getRacketItem() {
        return new ItemBuilder(Material.IRON_SHOVEL)
                .withAmount(1)
                .withCustomModelData(1001)
                .withName("&aBadminton Racket &7(Click to use)")
                .withNBTTag("accessor", "badminton_racket")
                .build();
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
