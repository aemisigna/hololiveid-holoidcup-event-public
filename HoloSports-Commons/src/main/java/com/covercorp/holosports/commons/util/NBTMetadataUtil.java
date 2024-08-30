package com.covercorp.holosports.commons.util;

import com.covercorp.holosports.commons.HoloSportsCommons;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class NBTMetadataUtil {
    public static ItemStack addString(final ItemStack item, final String name, final String value) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        final NamespacedKey key = new NamespacedKey(HoloSportsCommons.getCoreCommons(), name);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);

        item.setItemMeta(meta);

        return item;
    }

    public static boolean hasString(final ItemStack item, final String name) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        final NamespacedKey key = new NamespacedKey(HoloSportsCommons.getCoreCommons(), name);
        final PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(key, PersistentDataType.STRING);
    }

    public static String getString(final ItemStack item, final String name) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        final NamespacedKey key = new NamespacedKey(HoloSportsCommons.getCoreCommons(), name);
        final PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.get(key, PersistentDataType.STRING);
    }

    public static Entity addStringToEntity(final Entity entity, final String keyName, final String value) {
        final NamespacedKey key = new NamespacedKey(HoloSportsCommons.getCoreCommons(), keyName);
        entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);

        return entity;
    }

    public static String getEntityString(final Entity entity, final String keyName) {
        final NamespacedKey key = new NamespacedKey(HoloSportsCommons.getCoreCommons(), keyName);
        final PersistentDataContainer container = entity.getPersistentDataContainer();

        return container.get(key, PersistentDataType.STRING);
    }

    public static boolean hasEntityString(final Entity entity, final String keyName) {
        final NamespacedKey key = new NamespacedKey(HoloSportsCommons.getCoreCommons(), keyName);
        final PersistentDataContainer container = entity.getPersistentDataContainer();

        return container.has(key, PersistentDataType.STRING);
    }
}
