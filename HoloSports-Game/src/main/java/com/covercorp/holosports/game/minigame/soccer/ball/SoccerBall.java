package com.covercorp.holosports.game.minigame.soccer.ball;

import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.HoloSportsGame;

import com.covercorp.holosports.game.minigame.soccer.ball.stand.BallArmorStand;
import com.covercorp.holosports.game.minigame.soccer.player.player.ISoccerPlayer;
import com.covercorp.holosports.game.minigame.soccer.team.team.ISoccerTeam;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
public final class SoccerBall {
    private final UUID uniqueId;

    private BallArmorStand ballArmorStand;

    private final ItemStack customBallTexture = new ItemBuilder(Material.DIAMOND_BLOCK).withCustomModelData(1000).build();

    private @Nullable ISoccerPlayer ballTagger;

    @Setter(AccessLevel.PUBLIC) private Color particleColor;

    @Setter(AccessLevel.PUBLIC) private @Nullable ISoccerPlayer mandatoryTagger;
    @Setter(AccessLevel.PUBLIC) private boolean graced;

    @Setter(AccessLevel.PUBLIC) private boolean holded;

    private int gravityTaskId;

    public SoccerBall() {
        uniqueId = UUID.randomUUID();

        ballTagger = null;

        graced = false;
        holded = false;

        particleColor = Color.WHITE;
    }

    public boolean spawn(final Location location) {
        final World world = location.getWorld();
        if (world == null) return false;

        world.getEntities().stream().filter(entity -> entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.SLIME).forEach(entity -> {
            final boolean hasNbt = NBTMetadataUtil.hasEntityString(entity, "accessor");

            if (!hasNbt) return;

            if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("game_ball")) {
                HoloSportsGame.getHoloSportsGame().getLogger().info("Removing old soccer ball: " + entity.getUniqueId());
                entity.remove();
            }

            if (NBTMetadataUtil.getEntityString(entity, "accessor").equalsIgnoreCase("hitbox_game_ball")) {
                HoloSportsGame.getHoloSportsGame().getLogger().info("Removing old soccer ball: " + entity.getUniqueId());
                entity.remove();
            }
        });

        ballArmorStand = new BallArmorStand();

        final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setInvulnerable(false);
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setGravity(false);

        armorStand.setCustomNameVisible(false);

        final EntityEquipment entityEquipment = armorStand.getEquipment();
        if (entityEquipment == null) return false;

        entityEquipment.setHelmet(customBallTexture);

        NBTMetadataUtil.addStringToEntity(armorStand, "accessor", "game_ball");

        ballArmorStand.setBaseStand(armorStand);

        final Slime hitboxStand = (Slime) world.spawnEntity(location, EntityType.SLIME);

        hitboxStand.setInvisible(true);
        hitboxStand.setSilent(true);
        hitboxStand.setSize(2);
        hitboxStand.setAI(false);

        NBTMetadataUtil.addStringToEntity(hitboxStand, "accessor", "hitbox_game_ball");

        ballArmorStand.setHitboxStand(hitboxStand);

        armorStand.addPassenger(hitboxStand);

         //gravityTaskId = Bukkit.getScheduler().runTaskTimer(HoloSportsGame.getHoloSportsGame(), new GravityTask(this), 0L, 1L).getTaskId();

        // Spawn 20 FIREWORKS_SPARK particles at the location
        world.spawnParticle(Particle.FIREWORKS_SPARK, location, 20, 0.5, 0.5, 0.5, 0.0, null, true);


        return true;
    }

    public boolean despawn() {
        Bukkit.getScheduler().cancelTask(gravityTaskId);
        gravityTaskId = 0;

        if (ballArmorStand != null) {
            if (ballArmorStand.getBaseStand() != null) ballArmorStand.getBaseStand().remove();
        }

        if (ballArmorStand != null) {
            if (ballArmorStand.getHitboxStand() != null) ballArmorStand.getHitboxStand().remove();
        }

        setBallTagger(null);
        ballArmorStand = null;

        return true;
    }

    public void tagBall(final ISoccerPlayer soccerPlayer) {
        final ISoccerTeam team = soccerPlayer.getTeam();

        if (team == null) return;

        final Color color = (team.getColor().equals("RED") ? Color.RED : Color.AQUA);

    }

    public @Nullable Location getLocation() {
        if (ballArmorStand == null) return null;

        return ballArmorStand.getBaseStand().getLocation();
    }

    public boolean isOnGround() {
        if (ballArmorStand == null) return false;

        return ballArmorStand.getHitboxStand().isOnGround();
    }

    public void setBallTagger(final @Nullable ISoccerPlayer soccerPlayer) {
        ballTagger = soccerPlayer;
    }

    public @Nullable ISoccerPlayer getBallTagger() {
        return ballTagger;
    }

    public String getBallTaggerDetail() {
        if (ballTagger == null) return ChatColor.GRAY + "None";
        if (ballTagger.getTeam() == null) return ChatColor.GRAY + "None";

        return ChatColor.valueOf(ballTagger.getTeam().getColor() )+ ballTagger.getName();
    }
}
