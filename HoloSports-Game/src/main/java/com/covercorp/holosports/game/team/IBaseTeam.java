package com.covercorp.holosports.game.team;

import org.bukkit.ChatColor;

public interface IBaseTeam {
    String getIdentifier();
    String getName();
    String getDisplayName();

    String getBaseColor();
    ChatColor getColor();


}
