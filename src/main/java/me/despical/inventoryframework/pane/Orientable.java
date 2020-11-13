package me.despical.inventoryframework.pane;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import java.util.Locale;

/**
 * An interface for panes that can have different orientations
 *
 * @author Despicala
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public interface Orientable {

    /**
     * Gets the orientation of this outline pane
     *
     * @return the orientation
     * @since 1.0.1
     */
    @NotNull
    @Contract(pure = true)
    Orientation getOrientation();

    /**
     * Sets the orientation of this outline pane
     *
     * @param orientation the new orientation
     * @since 1.0.1
     */
    void setOrientation(@NotNull Orientation orientation);

    /**
     * Loads all elements regarding a {@link Orientable} {@link Pane} for the specified pane. The mutable pane contains
     * the changes made.
     *
     * @param orientable the orientable pane's elements to be applied
     * @param element the XML element for this pane
     * @since 1.0.1
     */
    static void load(@NotNull Orientable orientable, @NotNull Element element) {
        if (element.hasAttribute("orientation")) {
            orientable.setOrientation(Orientation.valueOf(element.getAttribute("orientation")
                .toUpperCase(Locale.getDefault())));
        }
    }

    /**
     * An orientation for outline panes
     *
     * @since 1.0.1
     */
    enum Orientation {

        /**
         * A horizontal orientation, will outline every item from the top-left corner going to the right and down
         *
         * @since 1.0.1
         */
        HORIZONTAL,

        /**
         * A vertical orientation, will outline every item from the top-left corner going down and to the right
         *
         * @since 0.3.0
         */
        VERTICAL
    }
}