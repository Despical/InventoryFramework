package me.despical.inventoryframework;

import me.despical.inventoryframework.pane.Pane;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Despical
 * <p>
 * Created at 30.07.2022
 */
public class GuiBuilder {

	private final Gui gui;

	public GuiBuilder(@NotNull GuiBuilder guiBuilder) {
		this.gui = guiBuilder.gui;
	}

	public GuiBuilder(@NotNull Plugin plugin) {
		this (plugin, 1, "");
	}

	public GuiBuilder(@NotNull Plugin plugin, int rows) {
		this (plugin, rows, "");
	}

	public GuiBuilder(@NotNull Plugin plugin, int rows, @NotNull String title) {
		this.gui = new Gui(plugin, rows, title);
	}

	public GuiBuilder rows(int rows) {
		this.gui.setRows(rows);
		this.gui.update();

		return this;
	}

	public GuiBuilder title(@NotNull String title) {
		this.gui.setTitle(title);
		this.gui.update();

		return this;
	}

	public GuiBuilder pane(@NotNull Pane pane) {
		this.gui.addPane(pane);

		return this;
	}

	public GuiBuilder pane(@NotNull Supplier<Pane> paneSupplier) {
		final Pane pane = paneSupplier.get();

		return this.pane(pane);
	}

	public GuiBuilder topClick(@NotNull Consumer<InventoryClickEvent> onTopClick) {
		this.gui.setOnTopClick(onTopClick);

		return this;
	}

	public GuiBuilder bottomClick(@NotNull Consumer<InventoryClickEvent> onBottomClick) {
		this.gui.setOnBottomClick(onBottomClick);

		return this;
	}

	public GuiBuilder globalClick(@NotNull Consumer<InventoryClickEvent> onGlobalClick) {
		this.gui.setOnGlobalClick(onGlobalClick);

		return this;
	}

	public GuiBuilder onOutsideClick(@NotNull Consumer<InventoryClickEvent> onOutsideClick) {
		this.gui.setOnOutsideClick(onOutsideClick);

		return this;
	}

	public GuiBuilder drag(@NotNull Consumer<InventoryDragEvent> onDrag) {
		this.gui.setOnDrag(onDrag);

		return this;
	}

	public GuiBuilder close(@NotNull Consumer<InventoryCloseEvent> onClose) {
		this.gui.setOnClose(onClose);

		return this;
	}

	public Gui build() {
		return this.gui;
	}

	public Gui show(@NotNull Player player) {
		this.gui.show(player);

		return this.build();
	}
}