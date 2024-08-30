package com.covercorp.holosports.game.minigame.badminton.arena.task.arrow;

import com.covercorp.holosports.commons.util.ItemBuilder;
import com.covercorp.holosports.commons.util.NBTMetadataUtil;
import com.covercorp.holosports.game.minigame.badminton.arena.BadmintonArena;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public final class SideArrowIndicatorTask implements Runnable {
    private final BadmintonArena badmintonArena;
    private final Location location;

    private ArmorStand firstArrow;
    private ArmorStand secondArrow;
    private ArmorStand thirdArrow;

    public SideArrowIndicatorTask(final BadmintonArena badmintonArena, final Location location) {
        this.badmintonArena = badmintonArena;
        this.location = location;

        final World world = location.getWorld();
        if (world == null) return;

        firstArrow = world.spawn(location, ArmorStand.class);
        secondArrow = world.spawn(location.clone().add(0.0, 0.0, 0.7), ArmorStand.class);
        thirdArrow = world.spawn(location.clone().add(0.0, 0.0, 1.4), ArmorStand.class);

        setupArmorStand(firstArrow);
        setupArmorStand(secondArrow);
        setupArmorStand(thirdArrow);
    }

    @Override
    public void run() {
        final World world = location.getWorld();
        if (world == null) return;

        final ImmutableList<ArmorStand> armorStands = ImmutableList.of(firstArrow, secondArrow, thirdArrow);

        Bukkit.getScheduler().runTaskLater(badmintonArena.getBadmintonMiniGame().getHoloSportsGame(), () -> {
            armorStands.forEach(ArmorStand::remove);
        }, 10L);
    }

    private void setupArmorStand(final ArmorStand armorStand) {
        armorStand.getEquipment().setHelmet(new ItemBuilder(Material.MAGMA_CREAM).withCustomModelData(1001).build());

        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setCollidable(false);
        armorStand.setMarker(true);

        NBTMetadataUtil.addStringToEntity(armorStand, "accessor", "badminton_arrow");
    }
}
