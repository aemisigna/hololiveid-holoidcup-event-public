package com.covercorp.holosports.game.minigame.soccer.ui;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.LoreDisplayArray;
import com.covercorp.holosports.game.minigame.soccer.SoccerMiniGame;
import com.covercorp.holosports.game.minigame.soccer.arena.SoccerArena;
import com.covercorp.holosports.game.minigame.soccer.inventory.SoccerGameItemCollection;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.ISoccerTeamHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;

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

public final class SoccerTeamChooseUI implements InventoryProvider {
    private final SoccerArena soccerArena;

    private final ISoccerPlayerHelper playerHelper;
    private final ISoccerTeamHelper teamHelper;

    public SoccerTeamChooseUI(final SoccerMiniGame soccerMiniGame) {
        this.soccerArena = soccerMiniGame.getArena();

        this.playerHelper = soccerMiniGame.getPlayerHelper();
        this.teamHelper = soccerMiniGame.getTeamHelper();
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        teamHelper.getTeamList().forEach(team -> {
            final ItemBuilder teamItemBuilder = new ItemBuilder(Material.valueOf(team.getColor() + "_STAINED_GLASS"))
                    .withName("&aTeam: &e" + team.getName() + " &7(" + team.getPlayers().stream().filter(p -> !p.isReferee()).toList().size() + "/6)")
                    .hideEnchantments();

            final LoreDisplayArray<String> lore = new LoreDisplayArray<>();

            if (team.getPlayers().stream().filter(p -> !p.isReferee()).toList().size() != 0) {
                lore.add("Team Members: ", ChatColor.GRAY);

                team.getPlayers().stream().filter(p -> !p.isReferee()).forEach(teamPlayer -> {
                    lore.add("&7- &f" + teamPlayer.getName() + " &8(" + (teamPlayer.getRole() == null ? "No Role" : teamPlayer.getRole()) + ")", ChatColor.GRAY);
                });
            }

            lore.add(" ");

            final Optional<ISoccerPlayer> soccerPlayerOptional = playerHelper.getPlayer(player.getUniqueId());

            if (soccerPlayerOptional.isEmpty()) {
                lore.add("&eClick to join this team &6and participate in the game&e.", ChatColor.YELLOW);

                teamItemBuilder.withLore(lore);

                final ItemStack teamItemStack = teamItemBuilder.build();

                inventoryContents.add(ClickableItem.of(teamItemStack, click -> {
                    final Optional<ISoccerPlayer> createdSoccerPlayerOptional = playerHelper.getOrCreatePlayer(player);
                    if (createdSoccerPlayerOptional.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "An error has occurred whilst creating your player instance.");
                        player.closeInventory();

                        return;
                    }

                    if (!NicePlayersUtil.isNicePlayer(player)) {
                        if (team.getPlayers().stream().filter(tPlayer -> !tPlayer.isReferee()).toList().size() >= 6) {
                            playerHelper.removePlayer(createdSoccerPlayerOptional.get().getUniqueId());

                            player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                            player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                            player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                            player.closeInventory();
                            return;
                        }
                    }

                    final ISoccerPlayer createdSoccerPlayer = createdSoccerPlayerOptional.get();

                    teamHelper.addPlayerToTeam(createdSoccerPlayer, team.getIdentifier());

                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&a&lYou are now participating in the next game!"));

                    player.closeInventory();
                }));

                return;
            }

            final ISoccerPlayer soccerPlayer = soccerPlayerOptional.get();
            final ISoccerTeam playerTeam = soccerPlayer.getTeam(); // Can be null

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
                    teamHelper.removePlayerFromTeam(soccerPlayer, team.getIdentifier());
                    playerHelper.removePlayer(soccerPlayer.getUniqueId());

                    SoccerGameItemCollection.resetPlayerHotbar(soccerPlayer);

                    player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2F,1.2F);
                    player.sendMessage(CommonUtil.colorize("&6&lYou are no longer participating in the next game."));

                    SoccerGameItemCollection.resetPlayerHotbar(soccerPlayer);

                    /*
                    if (NicePlayersUtil.isNicePlayer(player)) {
                        final Inventory inventory = player.getInventory();

                        inventory.setItem(4, SoccerGameItemCollection.getStartItem());
                        inventory.setItem(5, SoccerGameItemCollection.getResumeItem());
                        inventory.setItem(6, SoccerGameItemCollection.getStopItem());
                    }*/

                    player.closeInventory();
                    return;
                }

                if (!NicePlayersUtil.isNicePlayer(player)) {
                    if (team.getPlayers().stream().filter(tPlayer -> !tPlayer.isReferee()).toList().size() >= 6) {
                        player.sendMessage(CommonUtil.colorize("&c&lYou can't join this team right now."));
                        player.sendMessage(CommonUtil.colorize("&cThere's no enough space to join, if you need to"));
                        player.sendMessage(CommonUtil.colorize("&cparticipate, another player must leave the team."));

                        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.5F,1.5F);
                        player.closeInventory();
                        return;
                    }
                }

                teamHelper.addPlayerToTeam(soccerPlayer, team.getIdentifier());

                if (soccerPlayer.getRole() != null) {
                    SoccerGameItemCollection.resetPlayerHotbar(soccerPlayer);
                    SoccerGameItemCollection.setupPlayerHotbar(soccerArena, soccerPlayer, soccerPlayer.getRole());
                }

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
