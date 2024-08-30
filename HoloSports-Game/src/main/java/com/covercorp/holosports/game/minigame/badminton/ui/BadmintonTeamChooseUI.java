package com.covercorp.holosports.game.minigame.badminton.ui;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.LoreDisplayArray;
import com.covercorp.holosports.game.minigame.badminton.BadmintonMiniGame;
import com.covercorp.holosports.game.minigame.badminton.arena.inventory.BadmintonGameItemCollection;
import com.covercorp.holosports.game.minigame.badminton.player.IBadmintonPlayerHelper;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import com.covercorp.holosports.game.minigame.badminton.team.IBadmintonTeamHelper;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import com.covercorp.holosports.game.util.NicePlayersUtil;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class BadmintonTeamChooseUI implements InventoryProvider {
    private final IBadmintonPlayerHelper playerHelper;
    private final IBadmintonTeamHelper teamHelper;

    public BadmintonTeamChooseUI(final BadmintonMiniGame badmintonMiniGame) {
        this.playerHelper = badmintonMiniGame.getPlayerHelper();
        this.teamHelper = badmintonMiniGame.getTeamHelper();
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        teamHelper.getTeamList().forEach(team -> {
            final ItemBuilder teamItemBuilder = new ItemBuilder(Material.valueOf(team.getColor() + "_STAINED_GLASS"))
                    .withName("&aTeam: &e" + team.getName() + " &7(" + team.getPlayers().size() + "/2)")
                    .hideEnchantments();

            final LoreDisplayArray<String> lore = new LoreDisplayArray<>();

            if (team.getPlayers().size() != 0) {
                lore.add("Team Members: ", ChatColor.GRAY);

                team.getPlayers().forEach(teamPlayer -> {
                    lore.add("&7- &f" + teamPlayer.getName(), ChatColor.GRAY);
                });
            }

            lore.add(" ");

            final Optional<IBadmintonPlayer> badmintonPlayerOptional = playerHelper.getPlayer(player.getUniqueId());

            if (badmintonPlayerOptional.isEmpty()) {
                lore.add("&eClick to join this team &6and participate in the game&e.", ChatColor.YELLOW);

                teamItemBuilder.withLore(lore);

                final ItemStack teamItemStack = teamItemBuilder.build();

                inventoryContents.add(ClickableItem.of(teamItemStack, click -> {
                    final Optional<IBadmintonPlayer> createdBadmintonPlayerOptional = playerHelper.getOrCreatePlayer(player);
                    if (createdBadmintonPlayerOptional.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "An error has occurred whilst creating your player instance.");
                        player.closeInventory();

                        return;
                    }

                    if (team.getPlayers().size() >= 2) {
                        playerHelper.removePlayer(createdBadmintonPlayerOptional.get().getUniqueId());

                        player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                        player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                        player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                        player.closeInventory();
                        return;
                    }

                    final IBadmintonPlayer createdBadmintonPlayer = createdBadmintonPlayerOptional.get();

                    teamHelper.addPlayerToTeam(createdBadmintonPlayer, team.getIdentifier());

                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                    BadmintonGameItemCollection.setupPlayerHotbar(createdBadmintonPlayer);

                    player.closeInventory();
                }));

                return;
            }

            final IBadmintonPlayer badmintonPlayer = badmintonPlayerOptional.get();
            final IBadmintonTeam playerTeam = badmintonPlayer.getTeam(); // Can be null

            if (playerTeam != null) {
                if (playerTeam.getIdentifier().equals(team.getIdentifier())) {
                    lore.add("&eYou are already on this team.", ChatColor.YELLOW);
                    lore.add(" ");
                    lore.add("&6Click to leave the team and &cnot participate &6in the next game.", ChatColor.YELLOW);

                    teamItemBuilder.withEnchantment(Enchantment.ARROW_DAMAGE);
                } else {
                    lore.add("&eClick to join this team.", ChatColor.YELLOW);
                }
            } else {
                lore.add("&eClick to join this team &6and participate in the game&e.", ChatColor.YELLOW);
            }

            teamItemBuilder.withLore(lore);

            final ItemStack teamItemStack = teamItemBuilder.build();

            inventoryContents.add(ClickableItem.of(teamItemStack, click -> {
                if (playerTeam != null && playerTeam.getIdentifier().equals(team.getIdentifier())) {
                    BadmintonGameItemCollection.resetPlayerHotbar(badmintonPlayer);

                    teamHelper.removePlayerFromTeam(badmintonPlayer, team.getIdentifier());
                    playerHelper.removePlayer(badmintonPlayer.getUniqueId());

                    player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&6&lYou are no longer participating in the next game."));

                    if (NicePlayersUtil.isNicePlayer(player)) {
                        final Inventory inventory = player.getInventory();

                        inventory.setItem(5, BadmintonGameItemCollection.getStartItem());
                        inventory.setItem(6, BadmintonGameItemCollection.getStopItem());
                    }

                    player.closeInventory();
                    return;
                }

                if (team.getPlayers().size() >= 2) {
                    player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                    player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                    player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                    player.closeInventory();
                    return;
                }

                teamHelper.addPlayerToTeam(badmintonPlayer, team.getIdentifier());
                BadmintonGameItemCollection.setupPlayerHotbar(badmintonPlayer);

                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                player.closeInventory();
            }));
        });
    }

    @Override
    public void update(final Player player, final InventoryContents inventoryContents) {
        // Empty, for now
    }
}
