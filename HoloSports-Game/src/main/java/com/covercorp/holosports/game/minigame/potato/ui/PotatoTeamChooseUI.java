package com.covercorp.holosports.game.minigame.potato.ui;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.LoreDisplayArray;
import com.covercorp.holosports.game.minigame.potato.PotatoMiniGame;
import com.covercorp.holosports.game.minigame.potato.arena.inventory.PotatoGameItemCollection;
import com.covercorp.holosports.game.minigame.potato.player.IPotatoPlayerHelper;
import com.covercorp.holosports.game.minigame.potato.player.player.IPotatoPlayer;
import com.covercorp.holosports.game.minigame.potato.team.IPotatoTeamHelper;
import com.covercorp.holosports.game.minigame.potato.team.team.IPotatoTeam;
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

public final class PotatoTeamChooseUI implements InventoryProvider {
    private final IPotatoPlayerHelper playerHelper;
    private final IPotatoTeamHelper teamHelper;

    public PotatoTeamChooseUI(final PotatoMiniGame potatoMiniGame) {
        this.playerHelper = potatoMiniGame.getPlayerHelper();
        this.teamHelper = potatoMiniGame.getTeamHelper();
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

            final Optional<IPotatoPlayer> potatoPlayerOptional = playerHelper.getPlayer(player.getUniqueId());

            if (potatoPlayerOptional.isEmpty()) {
                lore.add("&eClick to join this team &6and participate in the game&e.", ChatColor.YELLOW);

                teamItemBuilder.withLore(lore);

                final ItemStack teamItemStack = teamItemBuilder.build();

                inventoryContents.add(ClickableItem.of(teamItemStack, click -> {
                    final Optional<IPotatoPlayer> createdPotatoPlayerOptional = playerHelper.getOrCreatePlayer(player);
                    if (createdPotatoPlayerOptional.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "An error has occurred whilst creating your player instance.");
                        player.closeInventory();

                        return;
                    }

                    if (team.getPlayers().size() >= 3) {
                        playerHelper.removePlayer(createdPotatoPlayerOptional.get().getUniqueId());

                        player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                        player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                        player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                        player.closeInventory();
                        return;
                    }

                    final IPotatoPlayer createdPotatoPlayer = createdPotatoPlayerOptional.get();

                    teamHelper.addPlayerToTeam(createdPotatoPlayer, team.getIdentifier());

                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                    PotatoGameItemCollection.setupPlayerHotbar(createdPotatoPlayer);

                    player.closeInventory();
                }));

                return;
            }

            final IPotatoPlayer potatoPlayer = potatoPlayerOptional.get();
            final IPotatoTeam playerTeam = potatoPlayer.getTeam(); // Can be null

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
                    PotatoGameItemCollection.resetPlayerHotbar(potatoPlayer);

                    teamHelper.removePlayerFromTeam(potatoPlayer, team.getIdentifier());
                    playerHelper.removePlayer(potatoPlayer.getUniqueId());

                    player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&6&lYou are no longer participating in the next game."));

                    if (NicePlayersUtil.isNicePlayer(player)) {
                        final Inventory inventory = player.getInventory();

                        inventory.setItem(5, PotatoGameItemCollection.getStartItem());
                        inventory.setItem(6, PotatoGameItemCollection.getStopItem());
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

                teamHelper.addPlayerToTeam(potatoPlayer, team.getIdentifier());
                PotatoGameItemCollection.setupPlayerHotbar(potatoPlayer);

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
