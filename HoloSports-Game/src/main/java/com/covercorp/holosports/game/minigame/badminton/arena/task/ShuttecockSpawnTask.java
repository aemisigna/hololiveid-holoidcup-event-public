package com.covercorp.holosports.game.minigame.badminton.arena.task;

import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.state.BadmintonMatchState;
import com.covercorp.holosports.game.minigame.badminton.team.team.IBadmintonTeam;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public final class ShuttecockSpawnTask implements Runnable {
    private final BadmintonArena badmintonArena;
    private final IBadmintonTeam serviceTeam;

    public ShuttecockSpawnTask(final BadmintonArena badmintonArena, final IBadmintonTeam serviceTeam) {
        this.badmintonArena = badmintonArena;
        this.serviceTeam = serviceTeam;
    }

    @Override
    public void run() {
        if (badmintonArena.getState() != BadmintonMatchState.GAME) return;

        badmintonArena.getArenaAnnouncer().sendGlobalMessage("&7&o[!] The shuttlecock has been spawned for team " + ChatColor.valueOf(serviceTeam.getColor()) + serviceTeam.getName() + "&7&o!");
        badmintonArena.teamService(serviceTeam);
    }
}
