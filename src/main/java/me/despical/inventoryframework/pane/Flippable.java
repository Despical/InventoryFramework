package me.despical.inventoryframework.pane;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

/**
 * An interface for panes that can be flipped
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public interface Flippable {

    /**
     * Sets whether this pane should flip its items horizontally
     *
     * @param flipHorizontally whether the pane should flip items horizontally
     * @since 1.0.1
     */
    void flipHorizontally(boolean flipHorizontally);

    /**
     * Sets whether this pane should flip its items vertically
     *
     * @param flipVertically whether the pane should flip items vertically
     * @since 1.0.1
     */
    void flipVertically(boolean flipVertically);

    /**
     * Gets whether this pane's items are flipped horizontally
     *
     * @return true if the items are flipped horizontally, false otherwise
     * @since 1.0.1
     */
    @Contract(pure = true)
    boolean isFlippedHorizontally();

    /**
     * Gets whether this pane's items are flipped vertically
     *
     * @return true if the items are flipped vertically, false otherwise
     * @since 1.0.1
     */
    @Contract(pure = true)
    boolean isFlippedVertically();

    /**
     * Loads all elements regarding a {@link Flippable} {@link Pane} for the specified pane. The mutable pane contains
     * the changes made.
     *
     * @param flippable the flippable pane's elements to be applied
     * @param element the XML element for this pane
     * @since 1.0.1
     */
    static void load(@NotNull Flippable flippable, @NotNull Element element) {
        if (element.hasAttribute("flipHorizontally"))
            flippable.flipHorizontally(Boolean.parseBoolean(element.getAttribute("flipHorizontally")));

        if (element.hasAttribute("flipVertically"))
            flippable.flipVertically(Boolean.parseBoolean(element.getAttribute("flipVertically")));
    }
}