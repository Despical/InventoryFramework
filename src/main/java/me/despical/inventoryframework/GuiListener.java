package me.despical.inventoryframework;

import me.despical.inventoryframework.pane.Pane;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Listens to events for {@link Gui}s. Only one instance of this class gets constructed.
 * (One instance per plugin, but plugins are supposed to shade and relocate IF.)
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class GuiListener implements Listener {

    /**
     * A collection of all {@link Gui} instances that have at least one viewer.
     */
    @NotNull
    private final Set<Gui> activeGuiInstances = new HashSet<>();

    /**
     * The main plugin instance.
     */
    @NotNull
    private final Plugin plugin;

    /**
     * Constructs a new listener
     *
     * @param plugin the main plugin
     * @since 1.0.1
     */
    public GuiListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles clicks in inventories
     *
     * @param event the event fired
     * @since 1.0.1
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Gui)) {
            return;
        }

        Gui gui = (Gui) event.getInventory().getHolder();
        Consumer<InventoryClickEvent> onOutsideClick = gui.getOnOutsideClick();

        if (onOutsideClick != null && event.getClickedInventory() == null) {
            onOutsideClick.accept(event);
            return;
        }

        Consumer<InventoryClickEvent> onGlobalClick = gui.getOnGlobalClick();

        if (onGlobalClick != null) {
            onGlobalClick.accept(event);
        }

        InventoryView view = event.getView();
        Inventory inventory = Gui.getInventory(view, event.getRawSlot());

        if (inventory == null) {
            return;
        }

        Consumer<InventoryClickEvent> onTopClick = gui.getOnTopClick();

        if (onTopClick != null && inventory.equals(view.getTopInventory())) {
            onTopClick.accept(event);
        }

        Consumer<InventoryClickEvent> onBottomClick = gui.getOnBottomClick();

        if (onBottomClick != null && inventory.equals(view.getBottomInventory())) {
            onBottomClick.accept(event);
        }

        if ((inventory.equals(view.getBottomInventory()) && gui.getState() == Gui.State.TOP) ||
            event.getCurrentItem() == null) {
            return;
        }

        List<Pane> panes = gui.getPanes();

        for (int i = panes.size() - 1; i >= 0; i--) {
            if (panes.get(i).click(gui, event, 0, 0, 9, gui.getRows() + 4))
                break;
        }
    }

    /**
     * Handles users picking up items while their bottom inventory is in use.
     *
     * @param event the event fired when an entity picks up an item
     * @since 1.0.3
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityPickupItem(@NotNull PlayerPickupItemEvent event) {
        InventoryHolder holder = event.getPlayer().getOpenInventory().getTopInventory().getHolder();

        if (!(holder instanceof Gui)) {
            return;
        }

        Gui gui = (Gui) holder;

        if (gui.getState() != Gui.State.BOTTOM) {
            return;
        }

        int leftOver = gui.getHumanEntityCache().add(event.getPlayer(), event.getItem().getItemStack());

        if (leftOver == 0) {
            event.getItem().remove();
        } else {
            ItemStack itemStack = event.getItem().getItemStack();

            itemStack.setAmount(leftOver);

            event.getItem().setItemStack(itemStack);
        }

        event.setCancelled(true);
    }

    /**
     * Handles small drag events which are likely clicks instead. These small drags will be interpreted as clicks and
     * will fire a click event.
     *
     * @param event the event fired
     * @since 1.0.3
     */
    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof Gui)) {
            return;
        }

		Gui gui = (Gui) event.getInventory().getHolder();
		Consumer<InventoryDragEvent> onDrag = gui.getOnDrag();

		if (onDrag != null) {
			onDrag.accept(event);
		}
    }

    /**
     * Handles closing in inventories
     *
     * @param event the event fired
     * @since 1.0.1
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Gui)) {
            return;
        }

        Gui gui = (Gui) event.getInventory().getHolder();

        Consumer<InventoryCloseEvent> onClose = gui.getOnClose();
        if (!gui.isUpdating() && onClose != null) {
            onClose.accept(event);
        }

        gui.getHumanEntityCache().restoreAndForget(event.getPlayer());

        if (gui.getViewerCount() == 1) {
            activeGuiInstances.remove(gui);
        }
    }

    /**
     * Registers newly opened inventories
     *
     * @param event the event fired
     * @since 1.0.1
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Gui)) {
            return;
        }

        Gui gui = (Gui) event.getInventory().getHolder();
        activeGuiInstances.add(gui);
    }

    /**
     * Handles the disabling of the plugin
     *
     * @param event the event fired
     * @since 1.0.1
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPluginDisable(@NotNull PluginDisableEvent event) {
        if (event.getPlugin() != plugin) {
            return;
        }

        int counter = 0;
		int maxCount = 10;
        while (!activeGuiInstances.isEmpty() && counter++ < maxCount) {
            for (Gui gui : new ArrayList<>(activeGuiInstances)) {
                for (HumanEntity viewer : gui.getViewers()) {
                    viewer.closeInventory();
                }
            }
        }

        if (counter == maxCount) {
			plugin.getLogger().warning("Unable to close GUIs on plugin disable: they keep getting opened "
					+ "(tried: " + maxCount + " times)");
		}
    }
}