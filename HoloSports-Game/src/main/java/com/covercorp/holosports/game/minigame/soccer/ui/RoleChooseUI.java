package com.covercorp.holosports.game.minigame.soccer.ui;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.LoreDisplayArray;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.player.role.SoccerRole;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

import com.covercorp.holosports.game.util.NicePlayersUtil;
import com.google.common.collect.ImmutableList;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RoleChooseUI implements InventoryProvider {
    private final SoccerArena soccerArena;
    private final ISoccerPlayer soccerPlayer;

    private final int maxGoalkeepers;
    private final int maxStandards;

    public RoleChooseUI(final SoccerMiniGame soccerMiniGame, final ISoccerPlayer soccerPlayer) {
        this.soccerArena = soccerMiniGame.getArena();
        this.soccerPlayer = soccerPlayer;

        maxGoalkeepers = 1;
        maxStandards = 5;
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        // Empty
    }

    @Override
    public void update(final Player player, final InventoryContents inventoryContents) {
        final ISoccerTeam team = soccerPlayer.getTeam();

        if (team == null) {
            player.sendMessage(ChatColor.RED + "Can't open the role inventory, is something wrong?");
            player.closeInventory();

            return;
        }

        final ItemBuilder refereeBuilder = new ItemBuilder(Material.BLAZE_POWDER)
                .withName("&aRole: &eReferee")
                .withCustomModelData(1000)
                .hideAttributes()
                .hideEnchantments()
                .withAmount(1);

        final LoreDisplayArray<String> refereeLore = new LoreDisplayArray<>();
        refereeLore.add("The referee is the one who will be in charge", ChatColor.GRAY);
        refereeLore.add("of the match, he will be the one who will", ChatColor.GRAY);
        refereeLore.add("start and pause the match.", ChatColor.GRAY);
        refereeLore.add(" ");

        if (soccerPlayer.getRole() == SoccerRole.REFEREE) {
            refereeLore.add("Chosen!", ChatColor.RED);
            refereeBuilder.withEnchantment(Enchantment.CHANNELING);
        } else {
            refereeLore.add("Click to set your role!", ChatColor.YELLOW);
        }

        refereeBuilder.withLore(refereeLore);

        if (NicePlayersUtil.isNicePlayer(player)) {
            inventoryContents.set(0, 0, ClickableItem.of(refereeBuilder.build(), click -> {
                /*if (!player.isOp()) {
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.2F, 1.2F);
                    player.sendMessage(CommonUtil.colorize("&chuh? you can't do that!"));
                    player.closeInventory();
                    return;
                }*/
                if (soccerPlayer.getRole() == SoccerRole.REFEREE) {
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.2F, 1.2F);
                    player.sendMessage(CommonUtil.colorize("&cYou have already chosen this role!"));
                    player.closeInventory();

                    return;
                }

                soccerPlayer.setRole(SoccerRole.REFEREE);

                SoccerGameItemCollection.setupPlayerHotbar(soccerArena, soccerPlayer, soccerPlayer.getRole());

                player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1.4F, 1.4F);
                player.sendMessage(CommonUtil.colorize("&eSelected role for the next match: &bReferee"));
                player.closeInventory();
            }));
        }

        final ItemBuilder goalkeeperBuilder = new ItemBuilder(Material.IRON_SHOVEL)
                .withName("&aRole: &eGoalkeeper &7(" + team.getGoalkeepers().size() + "/" + maxGoalkeepers + ")")
                .withCustomModelData(1000)
                .hideAttributes()
                .hideEnchantments()
                .withAmount(team.getGoalkeepers().size() == 0 ? 1 : team.getGoalkeepers().size());
        final LoreDisplayArray<String> goalkeeperLore = new LoreDisplayArray<>();

        goalkeeperLore.add("Goalkeepers must stay in the goal and stop", ChatColor.GRAY);
        goalkeeperLore.add("all the balls that are going to enter!", ChatColor.GRAY);
        goalkeeperLore.add("You will have special gloves to catch the ball.", ChatColor.GRAY);

        if (team.getGoalkeepers().size() >= 1) {
            goalkeeperLore.add(" ");
            goalkeeperLore.add("&eTeam " + ChatColor.valueOf(team.getColor()) + team.getName() + "&e Goalkeepers:", ChatColor.GRAY);

            team.getGoalkeepers().forEach(goalkeeper -> {
                goalkeeperLore.add("&f- &a" + goalkeeper.getName(), ChatColor.WHITE);
            });
        }

        goalkeeperLore.add(" ");

        if (team.getGoalkeepers().size() < 1) {
            if (team.getGoalkeepers().contains(soccerPlayer)) {
                goalkeeperLore.add("Chosen!", ChatColor.RED);
                goalkeeperBuilder.withEnchantment(Enchantment.CHANNELING);
            } else {
                goalkeeperLore.add("Click to set your role!", ChatColor.YELLOW);
            }
        }

        if (team.getGoalkeepers().size() >= 1) {
            if (team.getGoalkeepers().contains(soccerPlayer)) {
                goalkeeperLore.add("Chosen!", ChatColor.RED);
                goalkeeperBuilder.withEnchantment(Enchantment.CHANNELING);
            } else {
                goalkeeperLore.add("There's no space for more members in this role.", ChatColor.RED);
            }
        }

        goalkeeperBuilder.withLore(goalkeeperLore);

        final ItemStack goalkeeperItem = goalkeeperBuilder.build();

        inventoryContents.set(0, 3, ClickableItem.of(goalkeeperItem, click -> {
            if (team.getGoalkeepers().size() < 1) {
                if (team.getGoalkeepers().contains(soccerPlayer)) {
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.2F, 1.2F);
                    player.sendMessage(CommonUtil.colorize("&cYou have already chosen this role!"));
                    player.closeInventory();

                    return;
                }

                soccerPlayer.setRole(SoccerRole.GOALKEEPER);

                player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1.4F, 1.4F);
                player.sendMessage(CommonUtil.colorize("&eSelected role for the next match (Team: " + team.getName() + "): &bGoalkeeper"));
                player.closeInventory();

                SoccerGameItemCollection.setupPlayerHotbar(soccerArena, soccerPlayer, soccerPlayer.getRole());

                return;
            }

            if (team.getGoalkeepers().size() >= 1) {
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.2F, 1.2F);
                player.sendMessage(CommonUtil.colorize("&cThere's no enough space for you in this role. (Team: " + team.getName() + ")"));

                player.closeInventory();
            }
        }));

        final ItemBuilder standardBuilder = new ItemBuilder(Material.GOLDEN_HOE)
                .withName("&aRole: &eStandard &7(" + team.getStandards().size() + "/" + maxStandards + ")")
                .withCustomModelData(1000)
                .hideEnchantments()
                .hideAttributes()
                .withAmount(team.getStandards().size() == 0 ? 1 : team.getStandards().size());
        final LoreDisplayArray<String> standardLore = new LoreDisplayArray<>();

        standardLore.add("Standards must do everything possible to", ChatColor.GRAY);
        standardLore.add("have possession of the ball and score goals!", ChatColor.GRAY);
        standardLore.add("Standards will not have any special catch gloves.", ChatColor.GRAY);

        if (team.getStandards().size() >= 1) {
            standardLore.add(" ");
            standardLore.add("&eTeam " + ChatColor.valueOf(team.getColor()) + team.getName() + "&e Standards:", ChatColor.WHITE);

            team.getStandards().forEach(standard -> {
                standardLore.add("&f- &a" + standard.getName(), ChatColor.WHITE);
            });
        }

        standardLore.add(" ");

        if (team.getStandards().size() < 5) {
            if (team.getStandards().contains(soccerPlayer)) {
                standardLore.add("Chosen!", ChatColor.RED);
                standardBuilder.withEnchantment(Enchantment.CHANNELING);
            } else {
                standardLore.add("Click to set your role!", ChatColor.YELLOW);
            }
        }

        if (team.getStandards().size() >= 6) {
            if (team.getStandards().contains(soccerPlayer)) {
                standardLore.add("Chosen!", ChatColor.RED);
                standardBuilder.withEnchantment(Enchantment.CHANNELING);
            } else {
                standardLore.add("There's no space for more members in this role.", ChatColor.RED);
            }
        }

        standardBuilder.withLore(standardLore);

        final ItemStack standardItem = standardBuilder.build();

        inventoryContents.set(0, 5, ClickableItem.of(standardItem, click -> {
            /*if (team.getStandards().size() < 1) {

            }*/
            if (team.getStandards().size() >= 5) {
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.2F, 1.2F);
                player.sendMessage(CommonUtil.colorize("&cThere's no enough space for you in this role. (Team: " + team.getName() + ")"));

                player.closeInventory();

                return;
            }

            if (team.getStandards().contains(soccerPlayer)) {
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.2F, 1.2F);
                player.sendMessage(CommonUtil.colorize("&cYou have already chosen this role!"));
                player.closeInventory();

                return;
            }

            soccerPlayer.setRole(SoccerRole.STANDARD);

            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1.4F, 1.4F);
            player.sendMessage(CommonUtil.colorize("&eSelected role for the next match (Team: " + team.getName() + "): &bStandard"));
            player.closeInventory();

            SoccerGameItemCollection.setupPlayerHotbar(soccerArena, soccerPlayer, soccerPlayer.getRole());
        }));
    }
}
