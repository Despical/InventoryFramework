package me.despical.inventoryframework.pane;

import me.despical.inventoryframework.Gui;
import me.despical.inventoryframework.GuiItem;
import me.despical.inventoryframework.exception.XMLLoadException;
import me.despical.inventoryframework.util.GeometryUtil;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.function.Consumer;

/**
 * A pane for static items and stuff. All items will have to be specified a slot, or will be added in the next position.
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class StaticPane extends Pane implements Flippable, Rotatable, Fillable {

	/**
	 * A map of locations inside this pane and their item. The locations are stored in a way where the x coordinate is
	 * the key and the y coordinate is the value.
	 */
	@NotNull
	private final Map<Map.Entry<Integer, Integer>, GuiItem> items;

	/**
	 * The clockwise rotation of this pane in degrees
	 */
	private int rotation;

	/**
	 * Whether the items should be flipped horizontally and/or vertically
	 */
	private boolean flipHorizontally, flipVertically;

	public StaticPane(int x, int y, int length, int height, @NotNull Priority priority) {
		super(x, y, length, height, priority);

		this.items = new HashMap<>(length * height);
	}

	public StaticPane(int x, int y, int length, int height) {
		this(x, y, length, height, Priority.NORMAL);
	}

	public StaticPane(int length, int height) {
		this(0, 0, length, height);
	}

	@Override
	public void display(@NotNull Gui gui, @NotNull Inventory inventory, @NotNull PlayerInventory playerInventory,
						int paneOffsetX, int paneOffsetY, int maxLength, int maxHeight) {
		int length = Math.min(this.length, maxLength);
		int height = Math.min(this.height, maxHeight);

		items.entrySet().stream().filter(entry -> {
			GuiItem item = entry.getValue();
			Map.Entry<Integer, Integer> location = entry.getKey();

			return item.isVisible() && location.getKey() + paneOffsetX <= 9 && location.getValue() + paneOffsetY <= 6;
		}).forEach(entry -> {
			Map.Entry<Integer, Integer> location = entry.getKey();

			int x = location.getKey(), y = location.getValue();

			if (flipHorizontally)
				x = length - x - 1;

			if (flipVertically)
				y = height - y - 1;

			Map.Entry<Integer, Integer> coordinates = GeometryUtil.processClockwiseRotation(x, y, length, height,
				rotation);

			ItemStack item = entry.getValue().getItem();

			int finalRow = getY() + coordinates.getValue() + paneOffsetY;
			int finalColumn = getX() + coordinates.getKey() + paneOffsetX;

			if (finalRow >= gui.getRows()) {
				gui.setState(Gui.State.BOTTOM);

				if (finalRow == gui.getRows() + 3) {
					playerInventory.setItem(finalColumn, item);
				} else {
					playerInventory.setItem(((finalRow - gui.getRows()) + 1) * 9 + finalColumn, item);
				}
			} else {
				inventory.setItem(finalRow * 9 + finalColumn, item);
			}
		});
	}

	/**
	 * Adds a gui item at the specific spot in the pane. If the coordinates as specified by the x and y parameters is
	 * already occupied, that item will be replaced by the item parameter.
	 *
	 * @param item the item to set
	 * @param x    the x coordinate of the position of the item
	 * @param y    the y coordinate of the position of the item
	 */
	public void addItem(@NotNull GuiItem item, int x, int y) {
		items.keySet().removeIf(entry -> entry.getKey() == x && entry.getValue() == y);

		items.put(new AbstractMap.SimpleEntry<>(x, y), item);
	}

	/**
	 * Removes the specified item from the pane
	 *
	 * @param item the item to remove
	 * @since 1.0.1
	 */
	public void removeItem(@NotNull GuiItem item) {
		items.values().removeIf(guiItem -> guiItem.equals(item));
	}

	/**
	 * Removes the specified item from the pane
	 *
	 * @param x the x axis of item to remove
	 * @param y the y axis of item to remove
	 * @since 1.0.1
	 */
	public void removeItem(int x, int y) {
		items.remove(new AbstractMap.SimpleEntry<>(x, y));
	}

	@Override
	public boolean click(@NotNull Gui gui, @NotNull InventoryClickEvent event, int paneOffsetX, int paneOffsetY,
						 int maxLength, int maxHeight) {
		int length = Math.min(this.length, maxLength);
		int height = Math.min(this.height, maxHeight);

		int slot = event.getSlot();

		int x, y;

		if (Gui.getInventory(event.getView(), event.getRawSlot()).equals(event.getView().getBottomInventory())) {
			x = (slot % 9) - getX() - paneOffsetX;
			y = ((slot / 9) + gui.getRows() - 1) - getY() - paneOffsetY;

			if (slot / 9 == 0) {
				y = (gui.getRows() + 3) - getY() - paneOffsetY;
			}
		} else {
			x = (slot % 9) - getX() - paneOffsetX;
			y = (slot / 9) - getY() - paneOffsetY;
		}

		if (x < 0 || x >= length || y < 0 || y >= height)
			return false;

		if (onClick != null)
			onClick.accept(event);

		ItemStack itemStack = event.getCurrentItem();

		if (itemStack == null) {
			return false;
		}

		GuiItem clickedItem = findMatchingItem(items.values(), itemStack);

		if (clickedItem == null) {
			return false;
		}

		clickedItem.getAction().accept(event);
		return true;
	}

	@Override
	public void setRotation(int rotation) {
		if (length != height) {
			throw new UnsupportedOperationException("Length and height are different!");
		}

		if (rotation % 90 != 0) {
			throw new IllegalArgumentException("Rotation isn't divisible by 90!");
		}

		this.rotation = rotation % 360;
	}

	/**
	 * Fills all empty space in the pane with the given {@code itemStack} and adds the given action
	 *
	 * @param itemStack The {@link ItemStack} to fill the empty space with
	 * @param action    The action called whenever an interaction with the item happens
	 * @since 1.0.1
	 */
	public void fillWith(@NotNull ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> action) {
		Set<Map.Entry<Integer, Integer>> locations = this.items.keySet();
		GuiItem guiItem = GuiItem.of(itemStack, action);

		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getLength(); x++) {
				boolean found = false;

				for (Map.Entry<Integer, Integer> location : locations) {
					if (location.getKey() == x && location.getValue() == y) {
						found = true;
						break;
					}
				}

				if (!found) {
                    this.addItem(guiItem, x, y);
				}
			}
		}
	}

	/**
	 * Fills all empty space in the pane with the given {@code itemStack}
	 *
	 * @param itemStack The {@link ItemStack} to fill the empty space with
	 * @since 1.0.1
	 */
	public void fillWith(@NotNull ItemStack itemStack) {
		this.fillWith(itemStack, null);
	}

	@Override
	public void fillHorizontallyWith(@NotNull GuiItem guiItem, int line) {
        for (int x = 0; x < this.getLength(); x++) {
			// Will override the item if there is
        	this.addItem(guiItem, x, line);
        }
	}

    @Override
    public void fillVerticallyWith(@NotNull GuiItem guiItem, int line) {
	    for (int y = 0; y < this.getHeight(); y++) {
			// Will override the item if there is
			this.addItem(guiItem, line, y);
		}
	}

    @Override
    public void fillBorder(@NotNull GuiItem guiItem) {
        for (int slot : GeometryUtil.getBorders(this.getHeight())) {
            this.addItem(guiItem, slot % 9, slot / 9);
        }
    }

    @Override
    public void fillProgressBorder(@NotNull GuiItem full, @NotNull GuiItem empty, int progress) {
        if (progress > 100) throw new IllegalArgumentException("Progress cannot be more than 100!");

        int[] borders = GeometryUtil.getBorders(this.getHeight());
        int remaining = borders.length * progress / 100;

        for (int i : borders) {
			this.addItem(remaining > 0 ? full : empty, i % 9, i / 9);
			if (remaining > 0) remaining--;
		}
    }

    @NotNull
	@Override
	public Collection<GuiItem> getItems() {
		return items.values();
	}

	@NotNull
	public Set<Map.Entry<Integer, Integer>> getLocations() {
		return items.keySet();
	}

    @Override
    public void clear() {
        items.clear();
    }

	@NotNull
	@Contract(pure = true)
	@Override
	public Collection<Pane> getPanes() {
		return new HashSet<>();
	}

	@Override
	public void flipHorizontally(boolean flipHorizontally) {
		this.flipHorizontally = flipHorizontally;
	}

	@Override
	public void flipVertically(boolean flipVertically) {
		this.flipVertically = flipVertically;
	}

	@Contract(pure = true)
    @Override
	public int getRotation() {
		return rotation;
	}

	@Contract(pure = true)
    @Override
	public boolean isFlippedHorizontally() {
		return flipHorizontally;
	}

	@Contract(pure = true)
    @Override
	public boolean isFlippedVertically() {
		return flipVertically;
	}

	/**
	 * Loads an outline pane from a given element
	 *
	 * @param instance the instance class
	 * @param element  the element
	 * @return the outline pane
	 */
	@NotNull
	public static StaticPane load(@NotNull Object instance, @NotNull Element element) {
		try {
			StaticPane staticPane = new StaticPane(
				Integer.parseInt(element.getAttribute("length")),
				Integer.parseInt(element.getAttribute("height"))
            );

			load(staticPane, instance, element);
			Flippable.load(staticPane, element);
			Rotatable.load(staticPane, element);

			if (element.hasAttribute("populate"))
				return staticPane;

			NodeList childNodes = element.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);

				if (item.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element child = (Element) item;

				staticPane.addItem(loadItem(instance, child), Integer.parseInt(child.getAttribute("x")),
                    Integer.parseInt(child.getAttribute("y")));
			}

			return staticPane;
		} catch (NumberFormatException exception) {
			throw new XMLLoadException(exception);
		}
	}
}