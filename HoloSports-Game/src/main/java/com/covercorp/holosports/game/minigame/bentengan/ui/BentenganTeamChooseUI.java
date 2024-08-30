package com.covercorp.holosports.game.minigame.bentengan.ui;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.LoreDisplayArray;
import com.covercorp.holosports.game.minigame.bentengan.BentenganMiniGame;
import com.covercorp.holosports.game.minigame.bentengan.arena.inventory.BentenganGameItemCollection;
import com.covercorp.holosports.game.minigame.bentengan.player.IBentenganPlayerHelper;
import com.covercorp.holosports.game.minigame.bentengan.player.player.IBentenganPlayer;
import com.covercorp.holosports.game.minigame.bentengan.team.IBentenganTeamHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
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

public final class BentenganTeamChooseUI implements InventoryProvider {
    private final IBentenganPlayerHelper playerHelper;
    private final IBentenganTeamHelper teamHelper;

    public BentenganTeamChooseUI(final BentenganMiniGame bentenganMiniGame) {
        this.playerHelper = bentenganMiniGame.getPlayerHelper();
        this.teamHelper = bentenganMiniGame.getTeamHelper();
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        teamHelper.getTeamList().forEach(team -> {
            final ItemBuilder teamItemBuilder = new ItemBuilder(Material.valueOf(team.getColor() + "_STAINED_GLASS"))
                    .withName("&aTeam: &e" + team.getName() + " &7(" + team.getPlayers().size() + "/4)")
                    .hideEnchantments();

            final LoreDisplayArray<String> lore = new LoreDisplayArray<>();

            if (team.getPlayers().size() != 0) {
                lore.add("Team Members: ", ChatColor.GRAY);

                team.getPlayers().forEach(teamPlayer -> lore.add("&7- &f" + teamPlayer.getName(), ChatColor.GRAY));
            }

            lore.add(" ");

            final Optional<IBentenganPlayer> bentenganPlayerOptional = playerHelper.getPlayer(player.getUniqueId());

            if (bentenganPlayerOptional.isEmpty()) {
                lore.add("&eClick to join this team &6and participate in the game&e.", ChatColor.YELLOW);

                teamItemBuilder.withLore(lore);

                final ItemStack teamItemStack = teamItemBuilder.build();

                inventoryContents.add(ClickableItem.of(teamItemStack, click -> {
                    final Optional<IBentenganPlayer> createdBentenganPlayerOptional = playerHelper.getOrCreatePlayer(player);
                    if (createdBentenganPlayerOptional.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "An error has occurred whilst creating your player instance.");
                        player.closeInventory();

                        return;
                    }

                    if (team.getPlayers().size() >= 4) {
                        playerHelper.removePlayer(createdBentenganPlayerOptional.get().getUniqueId());

                        player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                        player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                        player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                        player.closeInventory();
                        return;
                    }

                    final IBentenganPlayer createdBentenganPlayer = createdBentenganPlayerOptional.get();

                    teamHelper.addPlayerToTeam(createdBentenganPlayer, team.getIdentifier());

                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                    BentenganGameItemCollection.setupPlayerHotbar(createdBentenganPlayer);

                    player.closeInventory();
                }));

                return;
            }

            final IBentenganPlayer bentenganPlayer = bentenganPlayerOptional.get();
            final IBentenganTeam playerTeam = bentenganPlayer.getTeam(); // Can be null

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
                    BentenganGameItemCollection.resetPlayerHotbar(bentenganPlayer);

                    teamHelper.removePlayerFromTeam(bentenganPlayer, team.getIdentifier());
                    playerHelper.removePlayer(bentenganPlayer.getUniqueId());

                    player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&6&lYou are no longer participating in the next game."));

                    if (NicePlayersUtil.isNicePlayer(player)) {
                        final Inventory inventory = player.getInventory();

                        inventory.setItem(5, BentenganGameItemCollection.getStartItem());
                        inventory.setItem(6, BentenganGameItemCollection.getStopItem());
                    }

                    player.closeInventory();
                    return;
                }

                if (team.getPlayers().size() >= 4) {
                    player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                    player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                    player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                    player.closeInventory();
                    return;
                }

                teamHelper.addPlayerToTeam(bentenganPlayer, team.getIdentifier());

                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                BentenganGameItemCollection.setupPlayerHotbar(bentenganPlayer);

                player.closeInventory();
            }));
        });
    }

    @Override
    public void update(final Player player, final InventoryContents inventoryContents) {
        // Empty, for now
    }
}
