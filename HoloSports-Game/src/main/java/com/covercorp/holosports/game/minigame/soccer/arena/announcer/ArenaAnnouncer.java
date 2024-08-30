package com.covercorp.holosports.game.minigame.soccer.arena.announcer;

import com.covercorp.holosports.commons.util.CommonUtil;
import com.covercorp.holosports.game.minigame.soccer.player.ISoccerPlayerHelper;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import com.covercorp.holosports.game.util.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class ArenaAnnouncer {

    private final ISoccerPlayerHelper soccerPlayerHelper;

    public ArenaAnnouncer(final ISoccerPlayerHelper soccerPlayerHelper) {
        this.soccerPlayerHelper = soccerPlayerHelper;
    }

    public void sendGlobalMessage(final String message) {
        /*soccerPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.sendMessage(CommonUtil.colorize(message));
        });*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(CommonUtil.colorize(message));
        });
    }

    public void sendGlobalCenteredMessage(final String message) {
        /*soccerPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            StringUtils.sendCenteredMessage(player, message);
        });*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            StringUtils.sendCenteredMessage(player, message);
        });
    }

    public void sendTeamMessage(final ISoccerTeam team, final String message) {
        team.getPlayers().forEach(teamPlayer -> {
            final Player player = Bukkit.getPlayer(teamPlayer.getUniqueId());
            if (player == null) return;

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1.5F, 1.5F);
            player.sendMessage(CommonUtil.colorize(message));
        });
    }
    
    public void sendTeamTitle(final ISoccerTeam team, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        team.getPlayers().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeIn);
        });
    }

    public void sendTeamSound(final ISoccerTeam team, Sound sound, float volume, float pitch) {
        team.getPlayers().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.playSound(player.getLocation(), sound, volume, pitch);
        });
    }

    public void sendGlobalTitle(final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        /*soccerPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeIn);
        });*/
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(CommonUtil.colorize(title), CommonUtil.colorize(subtitle), fadeIn, stay, fadeOut);
        });
    }

    public void sendGlobalActionBar(final String barContent) {
        /*soccerPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(barContent)));
        });*/

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommonUtil.colorize(barContent)));
        });
    }

    public void sendGlobalSound(final Sound sound, final float volume, final float pitch) {
        soccerPlayerHelper.getPlayerList().forEach(soccerPlayer -> {
            final Player player = Bukkit.getPlayer(soccerPlayer.getUniqueId());
            if (player == null) return;

            player.playSound(player.getLocation(), sound, volume, pitch);
        });
    }
}
