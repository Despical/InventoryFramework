package com.github.despical.inventoryframework.pane.component;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import com.github.despical.inventoryframework.Gui;
import com.github.despical.inventoryframework.GuiItem;
import com.github.despical.inventoryframework.exception.XMLLoadException;
import com.github.despical.inventoryframework.font.util.Font;
import com.github.despical.inventoryframework.pane.*;

/**
 * A label for displaying text.
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class Label extends OutlinePane {

    /**
     * The character set used for displaying the characters in this label
     */
    @NotNull
    private final Font font;

    /**
     * The text to be displayed
     */
    @NotNull
    private String text;

    /**
     * Creates a new label
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param length the length
     * @param height the height
     * @param priority the priority
     * @param font the character set
     * @since 1.0.1
     */
    public Label(int x, int y, int length, int height, @NotNull Priority priority, @NotNull Font font) {
        this(x, y, length, height, font);

        setPriority(priority);
    }

    /**
     * Creates a new label
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param length the length
     * @param height the height
     * @param font the character set
     * @since 1.0.1
     */
    public Label(int x, int y, int length, int height, @NotNull Font font) {
        this(length, height, font);

        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new label
     *
     * @param length the length
     * @param height the height
     * @param font the character set
     * @since 1.0.1
     */
    public Label(int length, int height, @NotNull Font font) {
        super(length, height);

        this.font = font;
        this.text = "";
    }

    /**
     * Sets the text to be displayed in this label
     *
     * @param text the new text
     * @since 1.0.1
     */
    public void setText(@NotNull String text) {
        this.text = text;

        clear();

        for (char character : text.toCharArray()) {
            ItemStack item = font.toItem(character);

            if (item == null) {
                item = font.toItem(Character.toUpperCase(character));
            }

            if (item == null) {
                item = font.toItem(Character.toLowerCase(character));
            }

            if (item == null) {
                item = font.getDefaultItem();
            }

            addItem(new GuiItem(item));
        }
    }

    @Override
    public boolean click(@NotNull Gui gui, @NotNull InventoryClickEvent event, int paneOffsetX, int paneOffsetY,
                         int maxLength, int maxHeight) {
        event.setCancelled(true);

        return super.click(gui, event, paneOffsetX, paneOffsetY, maxLength, maxHeight);
    }

    /**
     * Gets the text currently displayed in this label
     *
     * @return the text in this label
     * @since 1.0.1
     */
    @Contract(pure = true)
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * Gets the character set currently used for the text in this label
     *
     * @return the character set
     * @since 1.0.1
     */
    @Contract(pure = true)
    @NotNull
    public Font getFont() {
        return font;
    }

    /**
     * Loads a label from a given element
     *
     * @param instance the instance class
     * @param element  the element
     * @return the percentage bar
     */
    @NotNull
    @Contract(pure = true)
    public static Label load(@NotNull Object instance, @NotNull Element element) {
        int length;
        int height;

        try {
            length = Integer.parseInt(element.getAttribute("length"));
            height = Integer.parseInt(element.getAttribute("height"));
        } catch (NumberFormatException exception) {
            throw new XMLLoadException(exception);
        }

        Font font = null;

        if (element.hasAttribute("font")) {
            font = Font.fromName(element.getAttribute("font"));
        }

        if (font == null) {
            throw new XMLLoadException("Incorrect font specified for label");
        }

        Label label = new Label(length, height, font);

        Pane.load(label, instance, element);
        Orientable.load(label, element);
        Flippable.load(label, element);
        Rotatable.load(label, element);

        if (element.hasAttribute("populate")) {
            return label;
        }

        if (element.hasAttribute("text")) {
            label.setText(element.getAttribute("text"));
        }

        return label;
    }
}