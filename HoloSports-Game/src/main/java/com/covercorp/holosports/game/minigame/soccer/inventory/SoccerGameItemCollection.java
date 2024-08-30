package com.covercorp.holosports.game.minigame.soccer.inventory;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.HoloSportsGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.inventory.item.GameItemType;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class SoccerGameItemCollection {
    private final static String HOTBAR_ERROR_NULL_PLAYER = ChatColor.RED + "Could not setup game hotbar for %s";
    private final static String HOTBAR_ERROR = ChatColor.RED + "Could not setup your game hotbar.";
    private final static String HOTBAR_ERROR_TEAM = ChatColor.RED + "Can't setup your hotbar, you are not in a team!";

    public static void setupPlayerHotbar(final SoccerArena arena, final ISoccerPlayer soccerPlayer, final SoccerRole role) {
        final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());

        if (player == null) {
            HoloSportsGame.getHoloSportsGame().getLogger().severe(String.format(HOTBAR_ERROR_NULL_PLAYER, soccerPlayer.getUniqueId()));
            return;
        }

        final ISoccerTeam soccerTeam = soccerPlayer.getTeam();
        if (soccerTeam == null) {
            player.sendMessage(CommonUtil.colorize(HOTBAR_ERROR_TEAM));
            return;
        }

        final Inventory inventory = player.getInventory();

        inventory.clear();

        final ChatColor color = ChatColor.valueOf(soccerTeam.getIdentifier().toUpperCase());
        final ItemStack[] armor = new ItemStack[] {
                new ItemStack(Material.LEATHER_BOOTS, 1),
                new ItemStack(Material.LEATHER_LEGGINGS, 1),
                new ItemStack(Material.AIR),
                new ItemStack(Material.LEATHER_HELMET, 1)
        };

        for (final ItemStack itemStack : armor) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            final Color armorColor = Color.fromRGB(0, 0, 0);

            if (meta == null) continue;

            NBTMetadataUtil.addString(itemStack, "accessor", "game_armor");

            try {
                if (soccerPlayer.isReferee()) {
                    meta.setColor(Color.BLACK);
                } else {
                    meta.setColor((Color) armorColor.getClass().getDeclaredField(color.name()).get(armorColor));
                }
            } catch (NullPointerException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            itemStack.setItemMeta(meta);
        }

        player.getInventory().setArmorContents(armor);

        switch (role) {
            case STANDARD -> {
                inventory.setItem(0, getKickItem());
                inventory.setItem(1, getLongKickItem());

                inventory.setItem(4, getStartItem());
                inventory.setItem(5, getResumeItem());
                inventory.setItem(6, getStopItem());
            }
            case GOALKEEPER, REFEREE -> {
                inventory.setItem(0, getKickItem());
                inventory.setItem(1, getLongKickItem());
                inventory.setItem(2, getHoldItem());

                inventory.setItem(4, getStartItem());
                inventory.setItem(5, getResumeItem());
                inventory.setItem(6, getStopItem());
            }
            default -> {
                inventory.clear();
                player.sendMessage(HOTBAR_ERROR);
            }
        }
    }

    public static void resetPlayerHotbar(final ISoccerPlayer soccerPlayer) {
        final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());

        if (player == null) {
            HoloSportsGame.getHoloSportsGame().getLogger().severe(String.format(HOTBAR_ERROR_NULL_PLAYER, soccerPlayer.getUniqueId()));
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

    public static ItemStack getKickItem() {
        return new ItemBuilder(Material.IRON_HOE)
                .withAmount(1)
                .withCustomModelData(1000)
                .withName("&aKick &7(Click to use)")
                .withNBTTag("accessor", "kick")
                .withNBTTag("game_item_type", GameItemType.KICK.toString())
                .build();
    }

    public static ItemStack getLongKickItem() {
        return new ItemBuilder(Material.GOLDEN_HOE)
                .withAmount(1)
                .withCustomModelData(1000)
                .withName("&aLong Kick &7(Click to use)")
                .withNBTTag("accessor", "long_kick")
                .withNBTTag("game_item_type", GameItemType.LONG_KICK.toString())
                .build();
    }

    public static ItemStack getHoldItem() {
        return new ItemBuilder(Material.IRON_SHOVEL)
                .withAmount(1)
                .withCustomModelData(1000)
                .withName("&aHold &7(Click to use)")
                .withNBTTag("accessor", "hold")
                .withNBTTag("game_item_type", GameItemType.HOLD.toString())
                .build();
    }

    public static ItemStack getResumeItem() {
        return new ItemBuilder(Material.SLIME_BALL)
                .withAmount(1)
                .withName("&eResume game &7(Click to use)")
                .withCustomModelData(1000)
                .withNBTTag("accessor", "resume_game")
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
