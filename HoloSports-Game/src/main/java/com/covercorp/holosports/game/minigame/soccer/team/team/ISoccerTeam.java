package com.covercorp.holosports.game.minigame.soccer.team.team;

import com.covercorp.holosports.commons.util.Cuboid;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import org.bukkit.Location;

import java.util.List;
import java.util.Queue;
import java.util.Set;

public interface ISoccerTeam {
    String getIdentifier();
    String getName();

    String getColor();

    int getGoals();
    void setGoals(int goals);

    int getPenalties();
    void setPenalties(int penalties);

    Location getGoalKeeperSpawn();
    List<Location> getStandardSpawns();

    Cuboid getGoalCuboid();
    Cuboid getGoalSafeCuboid();

    Set<ISoccerPlayer> getPlayers();

    Queue<ISoccerPlayer> getPenaltyRotationQueue();

    void addPlayer(final ISoccerPlayer player);
    void removePlayer(final ISoccerPlayer player);

    List<ISoccerPlayer> getGoalkeepers();
    List<ISoccerPlayer> getStandards();
}
