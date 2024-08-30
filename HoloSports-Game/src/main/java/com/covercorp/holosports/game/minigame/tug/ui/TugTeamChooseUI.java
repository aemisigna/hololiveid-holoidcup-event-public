package com.covercorp.holosports.game.minigame.tug.ui;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.LoreDisplayArray;
import com.covercorp.holosports.game.minigame.tug.TugMiniGame;
import com.covercorp.holosports.game.minigame.tug.arena.inventory.TugGameItemCollection;
import com.covercorp.holosports.game.minigame.tug.player.ITugPlayerHelper;
import com.covercorp.holosports.game.minigame.tug.player.player.ITugPlayer;
import com.covercorp.holosports.game.minigame.tug.team.ITugTeamHelper;
import com.covercorp.holosports.game.minigame.tug.team.team.ITugTeam;
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

public final class TugTeamChooseUI implements InventoryProvider {
    private final ITugPlayerHelper playerHelper;
    private final ITugTeamHelper teamHelper;

    public TugTeamChooseUI(final TugMiniGame tugMiniGame) {
        this.playerHelper = tugMiniGame.getPlayerHelper();
        this.teamHelper = tugMiniGame.getTeamHelper();
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        teamHelper.getTeamList().forEach(team -> {
            final ItemBuilder teamItemBuilder = new ItemBuilder(Material.valueOf(team.getColor() + "_STAINED_GLASS"))
                    .withName("&aTeam: &e" + team.getName() + " &7(" + team.getPlayers().size() + "/3)")
                    .hideEnchantments();

            final LoreDisplayArray<String> lore = new LoreDisplayArray<>();

            if (team.getPlayers().size() != 0) {
                lore.add("Team Members: ", ChatColor.GRAY);

                team.getPlayers().forEach(teamPlayer -> {
                    lore.add("&7- &f" + teamPlayer.getName(), ChatColor.GRAY);
                });
            }

            lore.add(" ");

            final Optional<ITugPlayer> tugPlayerOptional = playerHelper.getPlayer(player.getUniqueId());

            if (tugPlayerOptional.isEmpty()) {
                lore.add("&eClick to join this team &6and participate in the game&e.", ChatColor.YELLOW);

                teamItemBuilder.withLore(lore);

                final ItemStack teamItemStack = teamItemBuilder.build();

                inventoryContents.add(ClickableItem.of(teamItemStack, click -> {
                    final Optional<ITugPlayer> createdTugPlayerOptional = playerHelper.getOrCreatePlayer(player);
                    if (createdTugPlayerOptional.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "An error has occurred whilst creating your player instance.");
                        player.closeInventory();

                        return;
                    }

                    if (team.getPlayers().size() >= 3) {
                        playerHelper.removePlayer(createdTugPlayerOptional.get().getUniqueId());

                        player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                        player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                        player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                        player.closeInventory();

                        return;
                    }

                    final ITugPlayer createdTugPlayer = createdTugPlayerOptional.get();

                    teamHelper.addPlayerToTeam(createdTugPlayer, team.getIdentifier());

                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                    TugGameItemCollection.setupPlayerHotbar(createdTugPlayer);

                    player.closeInventory();
                }));

                return;
            }

            final ITugPlayer tugPlayer = tugPlayerOptional.get();
            final ITugTeam playerTeam = tugPlayer.getTeam(); // Can be null

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
                    TugGameItemCollection.resetPlayerHotbar(tugPlayer);

                    teamHelper.removePlayerFromTeam(tugPlayer, team.getIdentifier());
                    playerHelper.removePlayer(tugPlayer.getUniqueId());

                    player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&6&lYou are no longer participating in the next game."));

                    if (NicePlayersUtil.isNicePlayer(player)) {
                        final Inventory inventory = player.getInventory();

                        inventory.setItem(5, TugGameItemCollection.getStartItem());
                        inventory.setItem(6, TugGameItemCollection.getStopItem());
                    }

                    player.closeInventory();
                    return;
                }

                if (team.getPlayers().size() >= 3) {
                    player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                    player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                    player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                    player.closeInventory();
                    return;
                }

                teamHelper.addPlayerToTeam(tugPlayer, team.getIdentifier());
                TugGameItemCollection.setupPlayerHotbar(tugPlayer);

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
