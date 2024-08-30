package com.covercorp.holosports.game.minigame.badminton.arena.announcer;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.badminton.player.IBadmintonPlayerHelper;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;

import com.covercorp.holosports.game.util.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class ArenaAnnouncer {

    private final IBadmintonPlayerHelper badmintonPlayerHelper;

    public ArenaAnnouncer(final IBadmintonPlayerHelper badmintonPlayerHelper) {
        this.badmintonPlayerHelper = badmintonPlayerHelper;
    }

    public void sendGlobalMessage(final String message) {
/*        badmintonPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.sendMessage(CommonUtil.colorize(message));
        });
*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(CommonUtil.colorize(message));
        });
    }

    public void sendGlobalCenteredMessage(final String message) {
        /*badmintonPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            StringUtils.sendCenteredMessage(player, message);
        });*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            StringUtils.sendCenteredMessage(player, message);
        });
    }
    
    public void sendTeamTitle(final IBadmintonTeam team, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        team.getPlayers().forEach(badmintonPlayer -> {
            final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());
            if (player == null) return;

            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeOut);
        });
    }

    public void sendTeamSound(final IBadmintonTeam team, Sound sound, float volume, float pitch) {
        team.getPlayers().forEach(badmintonPlayer -> {
            final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());
            if (player == null) return;

            player.playSound(player.getLocation(), sound, volume, pitch);
        });
    }

    public void sendGlobalTitle(final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        /*badmintonPlayerHelper.getPlayerList().forEach(badmintonPlayer -> {
            final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());
            if (player == null) return;

            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeOut);
        });*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeOut);
        });
    }

    public void sendGlobalActionBar(final String barContent) {
        /*badmintonPlayerHelper.getPlayerList().forEach(badmintonPlayer -> {
            final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());
            if (player == null) return;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(barContent)));
        });*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(barContent)));
        });
    }

    public void sendGlobalSound(final Sound sound, final float volume, final float pitch) {
        badmintonPlayerHelper.getPlayerList().forEach(badmintonPlayer -> {
            final Player player = Bukkit.getPlayer(badmintonPlayer.getUniqueId());
            if (player == null) return;

            player.playSound(player.getLocation(), sound, volume, pitch);
        });
    }
}
