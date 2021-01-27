package me.despical.inventoryframework.pane;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An interface for panes that can be fillable
 *
 * @author Despical
 * @since 1.0.8
 * <p>
 * Created at 18.01.2021
 */
public interface Fillable {

    /**
     * Fills specified row line horizontally with given {@code itemStack}
     *
     * @param itemStack The {@link ItemStack} to fill the empty space with
     * @param line      Line to fill with {@code itemStack}
     * @param action    The action called whenever an interaction with the item happens
     * @since 1.0.5
     */
    void fillHorizontallyWith(@NotNull ItemStack itemStack, int line, @Nullable Consumer<InventoryClickEvent> action);

    /**
     * Fills specified row line horizontally with given {@code itemStack}
     *
     * @param itemStack The {@link ItemStack} to fill the empty space with
     * @param line      Line to fill with {@code itemStack}
     * @since 1.0.5
     */
    void fillHorizontallyWith(@NotNull ItemStack itemStack, int line);

    /**
     * Fills specified row line vertically with given {@code itemStack}
     *
     * @param itemStack The {@link ItemStack} to fill the empty space with
     * @param line      Line to fill with {@code itemStack}
     * @param action    The action called whenever an interaction with the item happens
     * @since 1.0.8
     */
    void fillVerticallyWith(@NotNull ItemStack itemStack, int line, @Nullable Consumer<InventoryClickEvent> action);

    /**
     * Fills specified row line vertically with given {@code itemStack}
     *
     * @param itemStack The {@link ItemStack} to fill the empty space with
     * @param line      Line to fill with {@code itemStack}
     * @since 1.0.8
     */
    void fillVerticallyWith(@NotNull ItemStack itemStack, int line);

    /**
     * Fills inventory borders with given {@code itemStack}
     *
     * @param itemStack The {@link ItemStack} to fill the empty space with
     * @param action    The action called whenever an interaction with the item happens
     * @since 1.0.8
     */
    void fillBorder(@NotNull ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> action);

    /**
     * Fills inventory borders with given {@code itemStack}
     *
     * @param itemStack The {@link ItemStack} to fill the empty space with
     * @since 1.0.8
     */
    void fillBorder(@NotNull ItemStack itemStack);
}