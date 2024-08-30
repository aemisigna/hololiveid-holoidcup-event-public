package com.covercorp.holosports.game.minigame.badminton.arena.ball.hitbox;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Slime;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public final class BadmintonBallArmorStand {
    private ArmorStand armorStand;
    private Slime slime;

    private FallingBlock fallingBlock;
}
