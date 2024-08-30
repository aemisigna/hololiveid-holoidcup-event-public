package com.covercorp.holosports.game.minigame.bentengan.arena.announcer;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.bentengan.player.IBentenganPlayerHelper;
import com.covercorp.holosports.game.minigame.bentengan.team.team.IBentenganTeam;
import com.covercorp.holosports.game.util.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class ArenaAnnouncer {
    private final IBentenganPlayerHelper bentenganPlayerHelper;

    public ArenaAnnouncer(final IBentenganPlayerHelper bentenganPlayerHelper) {
        this.bentenganPlayerHelper = bentenganPlayerHelper;
    }

    public void sendGlobalMessage(final String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(CommonUtil.colorize(message));
        });
    }

    public void sendGlobalCenteredMessage(final String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            StringUtils.sendCenteredMessage(player, message);
        });
    }
    
    public void sendTeamTitle(final IBentenganTeam team, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        team.getPlayers().forEach(bentenganPlayer -> {
            final Player player = Bukkit.getPlayer(bentenganPlayer.getUniqueId());
            if (player == null) return;

            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeOut);
        });
    }

    public void sendTeamSound(final IBentenganTeam team, Sound sound, float volume, float pitch) {
        team.getPlayers().forEach(bentenganPlayer -> {
            final Player player = Bukkit.getPlayer(bentenganPlayer.getUniqueId());
            if (player == null) return;

            player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        });
    }

    public void sendGlobalTitle(final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeOut);
        });
    }

    public void sendGlobalActionBar(final String barContent) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(barContent)));
        });
    }

    public void sendGlobalSound(final Sound sound, final float volume, final float pitch) {
        bentenganPlayerHelper.getPlayerList().forEach(bentenganPlayer -> {
            final Player player = Bukkit.getPlayer(bentenganPlayer.getUniqueId());
            if (player == null) return;

            player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        });
    }
}
