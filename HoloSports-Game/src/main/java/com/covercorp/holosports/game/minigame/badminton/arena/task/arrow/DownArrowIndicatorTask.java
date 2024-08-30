package com.covercorp.holosports.game.minigame.badminton.arena.task.arrow;

import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public final class DownArrowIndicatorTask implements Runnable {
    private final BadmintonArena badmintonArena;
    private final Location location;

    @Getter(AccessLevel.PUBLIC) private ArmorStand armorStand;

    @Getter(AccessLevel.PUBLIC) private int rotationTaskId;
    @Getter(AccessLevel.PUBLIC) private int rotationTicks;

    @Getter(AccessLevel.PUBLIC) private final int rotationTicksLimit = 60;

    public DownArrowIndicatorTask(final BadmintonArena badmintonArena, final Location location) {
        this.badmintonArena = badmintonArena;
        this.location = location;
    }

    @Override
    public void run() {
        final World world = location.getWorld();
        if (world == null) return;

        armorStand = world.spawn(location, ArmorStand.class);

        armorStand.getEquipment().setHelmet(new ItemBuilder(Material.MAGMA_CREAM).withCustomModelData(1000).build());

        armorStand.setGlowing(false);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setCollidable(false);
        armorStand.setMarker(true);

        NBTMetadataUtil.addStringToEntity(armorStand, "accessor", "badminton_arrow");

        rotationTaskId = Bukkit.getScheduler().runTaskTimer(badmintonArena.getBadmintonMiniGame().getHoloSportsGame(), () -> {
            armorStand.setRotation(armorStand.getLocation().getYaw() + 20, armorStand.getLocation().getPitch());
            rotationTicks++;

            if (rotationTicks >= rotationTicksLimit) {
                Bukkit.getScheduler().cancelTask(rotationTaskId);
                armorStand.remove();
            }
        }, 0, 1).getTaskId();
    }
}
