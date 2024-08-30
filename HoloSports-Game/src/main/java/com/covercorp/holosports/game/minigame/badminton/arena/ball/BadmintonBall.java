package com.covercorp.holosports.game.minigame.badminton.arena.ball;

import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.hit.HitType;
import com.covercorp.holosports.game.minigame.badminton.arena.ball.hitbox.BadmintonBallArmorStand;
import com.covercorp.holosports.game.minigame.badminton.player.player.IBadmintonPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
public final class BadmintonBall {
    private final UUID uniqueId;

    private BadmintonBallArmorStand ballArmorStand;

    @Setter(AccessLevel.PUBLIC) private boolean flying;

    @Setter(AccessLevel.PUBLIC) private IBadmintonPlayer lastTagger;

    @Setter(AccessLevel.PUBLIC) private boolean smashed;

    @Setter(AccessLevel.PUBLIC) private HitType hitType = HitType.SERVICE;
    @Setter(AccessLevel.PUBLIC) private boolean serviceHit;
    
    private final static ItemStack SHUTTLECOCK_ITEM = new ItemBuilder(Material.SPRUCE_SAPLING).withCustomModelData(1000).build();
    
    public BadmintonBall(final BadmintonArena arena) {
        uniqueId = UUID.randomUUID();
    }
    
    public boolean spawn(final Location location) {
        final World world = location.getWorld();
        if (world == null) return false;

        ballArmorStand = new BadmintonBallArmorStand();
        
        final ArmorStand armorStand = world.spawn(location, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setGlowing(false);
        armorStand.setAI(false);
        armorStand.setSmall(true);
        armorStand.setCollidable(false);

        armorStand.getEquipment().setHelmet(new ItemBuilder(Material.SPRUCE_SAPLING).withCustomModelData(1000).build());

        NBTMetadataUtil.addStringToEntity(armorStand, "accessor", "game_ball");

        final Slime slime = (Slime) world.spawnEntity(location, EntityType.SLIME);

        slime.setInvisible(true);
        slime.setGravity(false);
        slime.setSilent(true);
        slime.setSize(5); // 3
        slime.setAI(true);
        slime.setAware(false);
        slime.setCollidable(false);

        NBTMetadataUtil.addStringToEntity(slime, "accessor", "hitbox_game_ball");

        //armorStand.addPassenger(slime);

        ballArmorStand.setArmorStand(armorStand);
        ballArmorStand.setSlime(slime);

        if (ballArmorStand.getFallingBlock() != null) {
            ballArmorStand.getFallingBlock().remove();
            ballArmorStand.setFallingBlock(null);
        }

        world.spawnParticle(Particle.VILLAGER_HAPPY, location, 10, 0.5, 0.5, 0.5, 0.0, null, true);
        world.playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0F, 0.5F);

        setHitType(HitType.SERVICE);
        setServiceHit(false);
        
        return true;
    }
    
    public void deSpawn() {
        if (ballArmorStand == null) return;

        if (ballArmorStand.getArmorStand() != null) {
            final ArmorStand armorStand = ballArmorStand.getArmorStand();
            final Location location = armorStand.getLocation();
            final World world = location.getWorld();
            if (world == null) return;

            location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location.add(0, 0.5, 0), 1, 0, 0, 0);

            ballArmorStand.getArmorStand().remove();
        }

        if (ballArmorStand.getSlime() != null) ballArmorStand.getSlime().remove();
        if (ballArmorStand.getFallingBlock() != null) ballArmorStand.getFallingBlock().remove();
    }

    public String getBallTaggerDetail() {
        if (lastTagger == null) return ChatColor.GRAY + "None";
        if (lastTagger.getTeam() == null) return ChatColor.GRAY + "None";

        return ChatColor.valueOf(lastTagger.getTeam().getColor()) + lastTagger.getName();
    }
}
