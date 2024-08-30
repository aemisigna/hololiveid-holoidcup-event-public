package com.covercorp.holosports.game.minigame.potato.arena.checkpoint;

import com.covercorp.holosports.commons.util.Cuboid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class RaceCheckpoint {
    private final Location checkpointLocation;
    private final Cuboid checkpointCuboid;
}
