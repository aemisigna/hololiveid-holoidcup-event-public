package com.covercorp.holosports.game.minigame.soccer.ball.stand;

import lombok.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Slime;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class BallArmorStand {
    private ArmorStand baseStand;
    private Slime hitboxStand;
}
