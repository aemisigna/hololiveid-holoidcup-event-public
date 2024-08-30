package com.covercorp.holosports.commons.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public final class ItemBuilder {
    private final ItemStack ITEM_STACK;

    public ItemBuilder(final Material mat) {
        this.ITEM_STACK = new ItemStack(mat);
    }

    public ItemBuilder(final ItemStack item) {
        this.ITEM_STACK = item;
    }

    public ItemBuilder withAmount(final int amount) {
        this.ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemBuilder withName(final String name) {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.setDisplayName(CommonUtil.colorize(name));
        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withNBTTag(final String name, final String content) {
        NBTMetadataUtil.addString(ITEM_STACK, name, content);

        return this;
    }

    public ItemBuilder withLore(final LoreDisplayArray<String> lore) {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.setLore(lore);
        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withCustomModelData(final int data) {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.setCustomModelData(data);

        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder hideEnchantments() {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder hideAttributes() {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withDurability(final int durability) {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        if (meta instanceof final Damageable damageable) {
            damageable.setDamage((short) durability);
            ITEM_STACK.setItemMeta(damageable);

            return this;
        }

        return this;
    }

    public ItemBuilder withPotionEffect(final PotionEffect effect, final Color color) {
        final PotionMeta meta = (PotionMeta) this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.setColor(color);
        meta.addCustomEffect(effect, true);
        this.ITEM_STACK.setItemMeta(meta);

        return this;
    }

    public ItemBuilder withEnchantment(final Enchantment enchantment, final int level) {
        this.ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder withEnchantment(final Enchantment enchantment) {
        this.ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder withType(final Material material) {
        this.ITEM_STACK.setType(material);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.setUnbreakable(true);
        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearLore() {
        final ItemMeta meta = this.ITEM_STACK.getItemMeta();
        if (meta == null) return this;

        meta.setLore(new ArrayList<>());
        this.ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (final Enchantment enchantment : this.ITEM_STACK.getEnchantments().keySet()) {
            this.ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemBuilder withColorArmour(final Color color) {
        final Material type = this.ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) this.ITEM_STACK.getItemMeta();
            if (meta == null) return this;

            meta.setColor(color);
            this.ITEM_STACK.setItemMeta((ItemMeta)meta);
            return this;
        }
        throw new IllegalArgumentException("withColor is only applicable for leather armor!");
    }

    public ItemStack build() {
        return this.ITEM_STACK;
    }
}
