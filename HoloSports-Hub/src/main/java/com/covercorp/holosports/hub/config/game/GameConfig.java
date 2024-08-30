package com.covercorp.holosports.hub.config.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class GameConfig {
    private final String display;
    private final String serverId;
    private final Location npcLocation;
    private final ItemStack item;
    private final int model;
    private final ChatColor color;
}
