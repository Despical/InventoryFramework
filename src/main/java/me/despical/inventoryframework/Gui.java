package me.despical.inventoryframework;

import me.despical.inventoryframework.exception.XMLLoadException;
import me.despical.inventoryframework.pane.*;
import me.despical.inventoryframework.pane.component.*;
import me.despical.inventoryframework.util.XMLUtil;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The base class of all GUIs
 * 
 * @author Despical
 * @since 1.0.1
 * <p>
 * Created at 04.09.2020
 */
public class Gui implements InventoryHolder {

    /**
     * A set of all panes in this inventory
     */
    @NotNull
    private final List<Pane> panes;

    /**
     * The inventory of this gui
     */
    @NotNull
    private Inventory inventory;

    /**
     * The title of this gui
     */
    @NotNull
    private String title;

    /**
     * The state of this gui
     */
    @NotNull
    private State state = State.TOP;

    /**
     * A player cache for storing player's inventories
     */
    @NotNull
    private final HumanEntityCache humanEntityCache = new HumanEntityCache();

    /**
     * The consumer that will be called once a players clicks in the top-half of the gui
     */
    @Nullable
    private Consumer<InventoryClickEvent> onTopClick;

    /**
     * The consumer that will be called once a players clicks in the bottom-half of the gui
     */
    @Nullable
    private Consumer<InventoryClickEvent> onBottomClick;

    /**
     * The consumer that will be called once a players clicks in the gui or in their inventory
     */
    @Nullable
    private Consumer<InventoryClickEvent> onGlobalClick;

    /**
     * The consumer that will be called once a player clicks outside the gui screen
     */
    @Nullable
    private Consumer<InventoryClickEvent> onOutsideClick;

	/**
	 * The consumer that will be called once a player drags any item in the gui or in their inventory
	 */
	@Nullable
	private Consumer<InventoryDragEvent> onDrag;

	/**
     * The consumer that will be called once a player closes the gui
     */
    @Nullable
    private Consumer<InventoryCloseEvent> onClose;

    /**
     * Whether this gui is updating (as invoked by {@link #update()}), true if this is the case, false otherwise. This
     * is used to indicate that inventory close events due to updating should be ignored.
     */
    private boolean updating = false;

    /**
     * The pane mapping which will allow users to register their own panes to be used in XML files
     */
    @NotNull
    private static final Map<String, BiFunction<Object, Element, Pane>> PANE_MAPPINGS = new HashMap<>();

    /**
     * Whether listeners have been registered by some gui
     */
    private static boolean hasRegisteredListeners;

    /**
     * Constructs a new GUI
     *
     * @param plugin the main plugin.
     * @param rows the amount of rows this gui should contain, in range 1..6.
     * @param title the title/name of this gui.
     */
    public Gui(@NotNull Plugin plugin, int rows, @NotNull String title) {
        if (!(rows >= 1 && rows <= 6)) {
            throw new IllegalArgumentException("Rows should be between 1 and 6");
        }

        this.panes = new ArrayList<>();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.inventory = plugin.getServer().createInventory(this, rows * 9, title);

        if (!hasRegisteredListeners) {
            plugin.getServer().getPluginManager().registerEvents(new GuiListener(plugin), plugin);

            hasRegisteredListeners = true;
        }
    }

    /**
     * Adds a pane to this gui
     *
     * @param pane the pane to add
     */
    public void addPane(@NotNull Pane pane) {
        this.panes.add(pane);

        this.panes.sort(Comparator.comparing(Pane::getPriority));
    }

    /**
     * Shows a gui to a player
     *
     * @param humanEntity the human entity to show the gui to
     */
    public void show(@NotNull HumanEntity humanEntity) {
        inventory.clear();

        setState(State.TOP);

        humanEntityCache.storeAndClear(humanEntity);

        panes.stream().filter(Pane::isVisible).forEach(pane -> pane.display(this, inventory,
            humanEntity.getInventory(), 0, 0, 9, getRows() + 4));

        if (state == State.TOP) {
            humanEntityCache.restoreAndForget(humanEntity);
        }

        humanEntity.openInventory(inventory);
    }

	/**
	 * Closes the inventory in the next server tick for all viewers.
	 */
	public void close() {
		JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());

		plugin.getServer().getScheduler().runTask(plugin, this::closeInstantly);
	}

	/**
	 * Closes the inventory for all viewers.
	 */
	public void closeInstantly() {
		Iterator<HumanEntity> iterator = getViewers().iterator();

		while (iterator.hasNext()) {
			iterator.next().closeInventory();
		}
	}

    /**
     * Sets the amount of rows for this inventory.
     * This will (unlike most other methods) directly update itself in order to ensure all viewers will still be viewing the new inventory as well.
     *
     * @param rows the amount of rows in range 1..6.
     */
    public void setRows(int rows) {
        if (!(rows >= 1 && rows <= 6)) {
            throw new IllegalArgumentException("Rows should be between 1 and 6");
        }

        List<HumanEntity> viewers = getViewers();

        this.inventory = Bukkit.createInventory(this, rows * 9, getTitle());

        viewers.forEach(humanEntity -> humanEntity.openInventory(inventory));
    }

    /**
     * Gets the count of {@link HumanEntity} instances that are currently viewing this GUI.
     *
     * @return the count of viewers
     * @since 1.0.1
     */
    @Contract(pure = true)
    public int getViewerCount() {
        return inventory.getViewers().size();
    }

    /**
     * Gets a mutable snapshot of the current {@link HumanEntity} viewers of this GUI.
     * This is a snapshot (copy) and not a view, therefore modifications aren't visible.
     *
     * @return a snapshot of the current viewers
     * @see #getViewerCount()
     * @since 1.0.1
     */
    @NotNull
    @Contract(pure = true)
    public List<HumanEntity> getViewers() {
        return new ArrayList<>(inventory.getViewers());
    }

    /**
     * Gets all the panes in this gui, this includes child panes from other panes
     *
     * @return all panes
     */
    @NotNull
    @Contract(pure = true)
    public List<Pane> getPanes() {
        List<Pane> panes = new ArrayList<>();

        this.panes.forEach(pane -> panes.addAll(pane.getPanes()));
        panes.addAll(this.panes);

        return panes;
    }

    /**
     * Sets the title for this inventory. This will (unlike most other methods) directly update itself in order
     * to ensure all viewers will still be viewing the new inventory as well.
     *
     * @param title the title
     */
    public void setTitle(@NotNull String title) {
        List<HumanEntity> viewers = getViewers();

        this.inventory = Bukkit.createInventory(this, this.inventory.getSize(), title);
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        viewers.forEach(humanEntity -> humanEntity.openInventory(inventory));
    }

    /**
     * Gets all the items in all underlying panes
     *
     * @return all items
     */
    @NotNull
    @Contract(pure = true)
    public Collection<GuiItem> getItems() {
        return getPanes().stream().flatMap(pane -> pane.getItems().stream()).collect(Collectors.toSet());
    }

    /**
     * Update the gui for everyone
     */
    public void update() {
        updating = true;

        getViewers().forEach(this::show);

        if (!updating)
            throw new AssertionError("Gui#isUpdating became false before Gui#update finished");

        updating = false;
    }

    /**
     * Calling this method will set the state of this gui. If this state is set to top state, it will restore all the
     * stored inventories of the players and will assume no pane extends into the bottom inventory part. If the state is
     * set to bottom state it will assume one or more panes overflow into the bottom half of the inventory and will
     * store all players' inventories and clear those.
     * <p>
     * Do not call this method if you just want the player's inventory to be cleared.
     *
     * @param state the new gui state
     * @since 1.0.1
     */
    public void setState(@NotNull State state) {
        this.state = state;

        if (state == State.TOP) {
            humanEntityCache.restoreAndForgetAll();
        } else if (state == State.BOTTOM) {
            inventory.getViewers().forEach(humanEntityCache::storeAndClear);
        }
    }

    /**
     * Gets the state of this gui
     *
     * @return the state
     * @since 1.0.1
     */
    @NotNull
    @Contract(pure = true)
    public State getState() {
        return state;
    }

    /**
     * Gets the human entity cache used for this gui
     *
     * @return the human entity cache
     * @see HumanEntityCache
     * @since 1.0.1
     */
    @NotNull
    @Contract(pure = true)
    protected HumanEntityCache getHumanEntityCache() {
        return humanEntityCache;
    }

    /**
     * Loads a Gui from a given input stream.
     * Returns null instead of throwing an exception in case of a failure.
     *
     * @param plugin the main plugin
     * @param instance the class instance for all reflection lookups
     * @param inputStream the file
     * @return the gui or null if the loading failed
     * @see #loadOrThrow(Plugin, Object, InputStream)
     */
    @Nullable
    public static Gui load(@NotNull Plugin plugin, @NotNull Object instance, @NotNull InputStream inputStream) {
        try {
            return loadOrThrow(plugin, instance, inputStream);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads a Gui from a given input stream.
     * Throws a {@link RuntimeException} instead of returning null in case of a failure.
     *
     * @param plugin the main plugin
     * @param instance the class instance for all reflection lookups
     * @param inputStream the file
     * @return the gui
     * @see #load(Plugin, Object, InputStream)
     */
    @NotNull
    public static Gui loadOrThrow(@NotNull Plugin plugin, @NotNull Object instance, @NotNull InputStream inputStream) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            Element documentElement = document.getDocumentElement();

            documentElement.normalize();

            Gui gui = new Gui(plugin, Integer.parseInt(documentElement.getAttribute("rows")), ChatColor
                    .translateAlternateColorCodes('&', documentElement.getAttribute("title")));

            if (documentElement.hasAttribute("field"))
                XMLUtil.loadFieldAttribute(instance, documentElement, gui);

            if (documentElement.hasAttribute("onTopClick")) {
                gui.setOnTopClick(XMLUtil.loadOnEventAttribute(instance,
                        documentElement, InventoryClickEvent.class, "onTopClick"));
            }

            if (documentElement.hasAttribute("onBottomClick")) {
                gui.setOnBottomClick(XMLUtil.loadOnEventAttribute(instance,
                        documentElement, InventoryClickEvent.class, "onBottomClick"));
            }

            if (documentElement.hasAttribute("onGlobalClick")) {
                gui.setOnGlobalClick(XMLUtil.loadOnEventAttribute(instance,
                        documentElement, InventoryClickEvent.class, "onGlobalClick"));
            }

            if (documentElement.hasAttribute("onOutsideClick")) {
                gui.setOnOutsideClick(XMLUtil.loadOnEventAttribute(instance,
                        documentElement, InventoryClickEvent.class, "onOutsideClick"));
            }

            if (documentElement.hasAttribute("onClose")) {
                gui.setOnClose(XMLUtil.loadOnEventAttribute(instance,
                        documentElement, InventoryCloseEvent.class, "onClose"));
            }

            if (documentElement.hasAttribute("onDrag")) {
                gui.setOnDrag(XMLUtil.loadOnEventAttribute(instance,
                        documentElement, InventoryDragEvent.class, "onDrag"));
            }

            if (documentElement.hasAttribute("populate")) {
                MethodUtils.invokeExactMethod(instance, "populate", gui, Gui.class);
            } else {
                NodeList childNodes = documentElement.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node item = childNodes.item(i);

                    if (item.getNodeType() == Node.ELEMENT_NODE)
                        gui.addPane(loadPane(instance, item));
                }
            }

            return gui;
        } catch (Exception e) {
            throw new XMLLoadException("Error loading " + plugin.getName() + "'s gui with associated class: "
                    + instance.getClass().getSimpleName(), e);
        }
    }

    /**
     * Set the consumer that should be called whenever this gui is clicked in.
     *
     * @param onTopClick the consumer that gets called
     */
    public void setOnTopClick(@Nullable Consumer<InventoryClickEvent> onTopClick) {
        this.onTopClick = onTopClick;
    }

    /**
     * Gets the top click event assigned to this gui, or null if there is no top click assigned.
     *
     * @return the top click
     * @since 1.0.1
     */
    @Nullable
    @Contract(pure = true)
    public Consumer<InventoryClickEvent> getOnTopClick() {
        return onTopClick;
    }

    /**
     * Set the consumer that should be called whenever the inventory is clicked in.
     *
     * @param onBottomClick the consumer that gets called
     */
    public void setOnBottomClick(@Nullable Consumer<InventoryClickEvent> onBottomClick) {
        this.onBottomClick = onBottomClick;
    }

    /**
     * Gets the bottom click event assigned to this gui, or null if there is no bottom click assigned.
     *
     * @return the bottom click
     * @since 1.0.1
     */
    @Nullable
    @Contract(pure = true)
    public Consumer<InventoryClickEvent> getOnBottomClick() {
        return onBottomClick;
    }

    /**
     * Set the consumer that should be called whenever this gui or inventory is clicked in.
     *
     * @param onGlobalClick the consumer that gets called
     */
    public void setOnGlobalClick(@Nullable Consumer<InventoryClickEvent> onGlobalClick) {
        this.onGlobalClick = onGlobalClick;
    }

    /**
     * Gets the global click event assigned to this gui, or null if there is no global click assigned.
     *
     * @return the global click
     * @since 1.0.1
     */
    @Nullable
    @Contract(pure = true)
    public Consumer<InventoryClickEvent> getOnGlobalClick() {
        return onGlobalClick;
    }

    /**
     * Set the consumer that should be called whenever a player clicks outside the gui.
     *
     * @param onOutsideClick the consumer that gets called
     * @since 1.0.1
     */
    public void setOnOutsideClick(@Nullable Consumer<InventoryClickEvent> onOutsideClick) {
        this.onOutsideClick = onOutsideClick;
    }

    /**
     * Gets the outside click event assigned to this gui, or null if there is no outside click assigned.
     *
     * @return the outside click
     * @since 1.0.1
     */
    @Nullable
    @Contract(pure = true)
    public Consumer<InventoryClickEvent> getOnOutsideClick() {
        return onOutsideClick;
    }

    /**
     * Set the consumer that should be called whenever this gui is clicked in.
     *
     * @param onLocalClick the consumer that gets called
     */
    public void setOnLocalClick(@NotNull Consumer<InventoryClickEvent> onLocalClick) {
        this.onTopClick = onLocalClick;
    }

	/**
	 * Set the consumer that should be called whenever player drags item in this gui.
	 *
	 * @param onDrag the consumer that gets called
	 */
	public void setOnDrag(@Nullable Consumer<InventoryDragEvent> onDrag) {
		this.onDrag = onDrag;
	}

	/**
	 * Gets the global click event assigned to this gui, or null if there is no global click assigned.
	 *
	 * @return the global click
	 * @since 1.0.6
	 */
	@Nullable
	@Contract(pure = true)
	public Consumer<InventoryDragEvent> getOnDrag() {
		return onDrag;
	}

	/**
     * Set the consumer that should be called whenever this gui is closed.
     *
     * @param onClose the consumer that gets called
     */
    public void setOnClose(@Nullable Consumer<InventoryCloseEvent> onClose) {
        this.onClose = onClose;
    }

    /**
     * Gets the on close event assigned to this gui, or null if no close event is assigned.
     *
     * @return the on close event
     * @since 1.0.1
     */
    @Nullable
    @Contract(pure = true)
    public Consumer<InventoryCloseEvent> getOnClose() {
        return onClose;
    }

    /**
     * Returns the amount of rows this gui currently has
     *
     * @return the amount of rows
     */
    public int getRows() {
        return inventory.getSize() / 9;
    }

    /**
     * Returns the title of this gui
     *
     * @return the title
     */
    @NotNull
    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets whether this gui is being updated, as invoked by {@link #update()}. This returns true if this is the case
     * and false otherwise.
     *
     * @return whether this gui is being updated
     * @since 1.0.1
     */
    @Contract(pure = true)
    public boolean isUpdating() {
        return updating;
    }

    public static Inventory getInventory(InventoryView view, int rawSlot) {
        if (rawSlot == InventoryView.OUTSIDE || rawSlot == -1) {
            return null;
        }
        return rawSlot < view.getTopInventory().getSize()
                ? view.getTopInventory()
                : view.getBottomInventory();
    }

    /**
     * Registers a property that can be used inside an XML file to add additional new properties.
     *
     * @param attributeName the name of the property. This is the same name you'll be using to specify the property
     *                      type in the XML file.
     * @param function how the property should be processed. This converts the raw text input from the XML node value
     *                 into the correct object type.
     * @throws IllegalArgumentException when a property with this name is already registered.
     */
    public static void registerProperty(@NotNull String attributeName, @NotNull Function<String, Object> function) {
        Pane.registerProperty(attributeName, function);
    }

    /**
     * Registers a name that can be used inside an XML file to add custom panes
     *
     * @param name the name of the pane to be used in the XML file
     * @param biFunction how the pane loading should be processed
     * @throws IllegalArgumentException when a pane with this name is already registered
     */
    public static void registerPane(@NotNull String name, @NotNull BiFunction<Object, Element, Pane> biFunction) {
        if (PANE_MAPPINGS.containsKey(name)) {
            throw new IllegalArgumentException("pane name '" + name + "' is already registered");
        }

        PANE_MAPPINGS.put(name, biFunction);
    }

    /**
     * Loads a pane by the given instance and node
     *
     * @param instance the instance
     * @param node the node
     * @return the pane
     */
    @NotNull
    public static Pane loadPane(@NotNull Object instance, @NotNull Node node) {
        return PANE_MAPPINGS.get(node.getNodeName()).apply(instance, (Element) node);
    }

    /**
     * The gui state
     *
     * @since 1.0.1
     */
    public enum State {

        /**
         * This signals that only the top-half of the Gui is in use and the player's inventory will stay like it is
         *
         * @since 1.0.1
         */
        TOP,

        /**
         * This signals that the bottom-hal of the Gui is in use and the player's inventory will be cleared and stored
         *
         * @since 1.0.1
         */
        BOTTOM
    }

    static {
        registerPane("masonrypane", MasonryPane::load);
        registerPane("outlinepane", OutlinePane::load);
        registerPane("paginatedpane", PaginatedPane::load);
        registerPane("staticpane", StaticPane::load);
        registerPane("cyclebutton", CycleButton::load);
        registerPane("label", Label::load);
        registerPane("percentagebar", PercentageBar::load);
        registerPane("slider", Slider::load);
        registerPane("togglebutton", ToggleButton::load);
    }
}