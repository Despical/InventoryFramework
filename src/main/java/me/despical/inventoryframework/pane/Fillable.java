package me.despical.inventoryframework.pane;

import me.despical.inventoryframework.GuiItem;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for panes that can be fillable.
 *
 * @author Despical
 * @since 1.0.8
 * <p>
 * Created at 18.01.2021
 */
public interface Fillable {

    /**
     * Fills specified row line horizontally with given gui item.
     *
     * @param guiItem to fill the empty space with
     * @param line    to fill with {@code guiItem}
     * @since 1.0.5
     */
    void fillHorizontallyWith(@NotNull GuiItem guiItem, int line);

    /**
     * Fills specified row line vertically with given gui item.
     *
     * @param guiItem to fill the empty space with
     * @param line    to fill with given gui item
     * @since 1.0.8
     */
    void fillVerticallyWith(@NotNull GuiItem guiItem, int line);

    /**
     * Fills inventory borders with given gui item.
     *
     * @param guiItem to fill the empty space with
     * @since 1.0.8
     */
    void fillBorder(@NotNull GuiItem guiItem);


    /**
     * Fills inventory borders with given gui item.
     *
     * @param full     to fill the progressed space with
     * @param empty    to fill the empty space with
     * @param progress percentage of items to fill
     * @since 1.1.0
     */
    void fillProgressBorder(@NotNull GuiItem full, @NotNull GuiItem empty, int progress);
}
