package com.covercorp.holosports.commons.util;

import com.covercorp.holosports.commons.HoloSportsCommons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public final class BungeeUtil {
    public static void sendPlayerToServer(final Player player, final String server) {
        try {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.5F);
            //player.sendMessage(ChatColor.GREEN + "[HoloSports-Commons] Connecting to " + ChatColor.YELLOW + server + ChatColor.GREEN + "...");

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(server);

            player.sendPluginMessage(HoloSportsCommons.getCoreCommons(), "BungeeCord", byteArrayOutputStream.toByteArray());

            byteArrayOutputStream.close();
            dataOutputStream.close();
        } catch (final Exception exception) {
            player.sendMessage(ChatColor.RED + "[HoloSports-Commons] An error occurred whilst connecting to " + server);
        }
    }

    public static void sendToServerButAwesome(final Plugin plugin, final Player player, final String server) {
        player.sendTitle("\uE299\uE299\uE299\uE299\uE299\uE299\uE299", "", 10, 80, 5);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BungeeUtil.sendPlayerToServer(player, server);
        }, 25L);
    }
}
