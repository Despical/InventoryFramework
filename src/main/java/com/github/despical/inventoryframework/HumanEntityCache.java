package com.github.despical.inventoryframework;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for containing players and their inventory state for later use
 *
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class HumanEntityCache {

    /**
     * A map containing the player's and their inventory contents. The ItemStack[] contains only the hotbar and
     * inventory contents. 0-8 is the hotbar, with 9-35 being the inventory both starting in the top-left corner and
     * continuing in reading order.
     */
    private final Map<HumanEntity, ItemStack[]> inventories = new HashMap<>();

    /**
     * Stores this player's inventory in the cache. If the player was already stored, their cache will be overwritten.
     * Clears the player's inventory afterwards.
     *
     * @param humanEntity the human entity to keep in the cache
     */
    public void storeAndClear(@NotNull HumanEntity humanEntity) {
        store(humanEntity);

        Inventory inventory = humanEntity.getInventory();
        for (int i = 0; i < 36; i++) {
            inventory.clear(i);
        }
    }

    /**
     * Stores this player's inventory in the cache. If the player was already stored, their cache will be overwritten.
     *
     * @param humanEntity the human entity to keep in the cache
     * @since 1.0.1
     */
    public void store(@NotNull HumanEntity humanEntity) {
        ItemStack[] items = new ItemStack[36];

        for (int i = 0 ; i < 36; i++) {
            items[i] = humanEntity.getInventory().getItem(i);
        }

        inventories.put(humanEntity, items);
    }

    /**
     * Restores the contents of the specified human entity, clearing the cache afterwards.
     * This method will fail silently if no cache is available.
     *
     * @param humanEntity the human entity to restore its cache for
     * @since 1.0.1
     */
    public void restoreAndForget(@NotNull HumanEntity humanEntity) {
        restore(humanEntity);
        clearCache(humanEntity);
    }

    /**
     * Restores all players' contents into their inventory, clearing the cache afterwards.
     *
     * @since 1.0.1
     */
    public void restoreAndForgetAll() {
        restoreAll();
        clearCache();
    }

    /**
     * Restores the contents of the specified human entity. This method will fail silently if no cache is available. The
     * cache will not be cleared.
     *
     * @param humanEntity the human entity to restore its cache for
     * @since 1.0.1
     */
    public void restore(@NotNull HumanEntity humanEntity) {
        ItemStack[] items = inventories.get(humanEntity);

        if (items == null) {
            return;
        }

        for (int i = 0; i < items.length; i++) {
            humanEntity.getInventory().setItem(i, items[i]);
        }
    }

    /**
     * Restores all players' contents into their inventory. The cache will not be cleared.
     *
     * @since 1.0.1
     */
    public void restoreAll() {
        inventories.keySet().forEach(this::restore);
    }

    /**
     * Clear the cache for the specified human entity
     *
     * @param humanEntity the human entity to clear the cache for
     * @since 1.0.1
     */
    public void clearCache(@NotNull HumanEntity humanEntity) {
        inventories.remove(humanEntity);
    }

    /**
     * This clears the cache.
     *
     * @since 1.0.1
     */
    public void clearCache() {
        inventories.clear();
    }
}