package me.mrCookieSlime.CSCoreLibPlugin.general.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.mrCookieSlime.CSCoreLibPlugin.general.Math.Calculator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestMenu {
	
	/**
	 * Checks whether the Config contains the specified Path
	 *
	 * @param  path The path in the Config File
	 * @return      True/false
	 */ 
	
	boolean clickable;
	String title;
	Inventory inv;
	List<ItemStack> items;
	Map<Integer, MenuClickHandler> handlers;
	MenuOpeningHandler open;
	MenuCloseHandler close;
	MenuClickHandler playerclick;
	
	/**
	 * Creates a new ChestMenu with the specified
	 * Title
	 *
	 * @param  title The title of the Menu
	 */ 
	public ChestMenu(String title) {
		this.title = ChatColor.translateAlternateColorCodes('&', title);
		this.clickable = false;
		this.items = new ArrayList<ItemStack>();
		this.handlers = new HashMap<Integer, MenuClickHandler>();
		this.open = new MenuOpeningHandler() {
			
			@Override
			public void onOpen(Player p) {
			}
		};
		this.close = new MenuCloseHandler() {
			
			@Override
			public void onClose(Player p) {
			}
		};
		this.playerclick = new MenuClickHandler() {
			
			@Override
			public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
				return isPlayerInventoryClickable();
			}
		};
	}
	
	/**
	 * Toggles whether Players can access there
	 * Inventory while viewing this Menu
	 *
	 * @param  clickable Whether the Player can access his Inventory
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu setPlayerInventoryClickable(boolean clickable) {
		this.clickable = clickable;
		return this;
	}
	
	/**
	 * Returns whether the Player's Inventory is
	 * accessible while viewing this Menu
	 *
	 * @return      Whether the Player Inventory is clickable
	 */ 
	public boolean isPlayerInventoryClickable() {
		return clickable;
	}
	
	/**
	 * Adds a ClickHandler to ALL Slots of the
	 * Player's Inventory
	 *
	 * @param  handler The MenuClickHandler
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu addPlayerInventoryClickHandler(MenuClickHandler handler) {
		this.playerclick = handler;
		return this;
	}
	
	/**
	 * Adds an Item to the Inventory in that Slot
	 *
	 * @param  slot The Slot in the Inventory
	 * @param  item The Item for that Slot
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu addItem(int slot, ItemStack item) {
		final int size = this.items.size();
		if (size > slot) this.items.set(slot, item);
		else {
			for (int i = 0; i < slot - size; i++) {
				this.items.add(null);
			}
			this.items.add(item);
		}
		return this;
	}
	
	/**
	 * Adds an Item to the Inventory in that Slot
	 * as well as a Click Handler
	 *
	 * @param  slot The Slot in the Inventory
	 * @param  item The Item for that Slot
	 * @param  handler The MenuClickHandler for that Slot
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu addItem(int slot, ItemStack item, MenuClickHandler clickHandler) {
		addItem(slot, item);
		addMenuClickHandler(slot, clickHandler);
		return this;
	}
	
	/**
	 * Returns the ItemStack in that Slot
	 *
	 * @param  slot The Slot in the Inventory
	 * @return      The ItemStack in that Slot
	 */ 
	public ItemStack getItemInSlot(int slot) {
		setup();
		return this.inv.getItem(slot);
	}
	
	/**
	 * Executes a certain Action upon clicking an
	 * Item in the Menu
	 *
	 * @param  slot The Slot in the Inventory
	 * @param  handler The MenuClickHandler
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu addMenuClickHandler(int slot, MenuClickHandler handler) {
		this.handlers.put(slot, handler);
		return this;
	}
	
	/**
	 * Executes a certain Action upon opening
	 * this Menu
	 *
	 * @param  handler The MenuOpeningHandler
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu addMenuOpeningHandler(MenuOpeningHandler handler) {
		this.open = handler;
		return this;
	}
	
	/**
	 * Executes a certain Action upon closing
	 * this Menu
	 *
	 * @param  handler The MenuCloseHandler
	 * @return      The ChestMenu Instance
	 */ 
	public ChestMenu addMenuCloseHandler(MenuCloseHandler handler) {
		this.close = handler;
		return this;
	}
	
	/**
	 * Finishes the Creation of the Menu
	 *
	 * @return      The ChestMenu Instance
	 */ 
	@Deprecated
	public ChestMenu build() {
		return this;
	}
	
	/**
	 * Returns an Array containing the Contents
	 * of this Inventory
	 *
	 * @return      The Contents of this Inventory
	 */ 
	public ItemStack[] getContents() {
		setup();
		return this.inv.getContents();
	}
	
	private void setup() {
		if (this.inv != null) return;
		this.inv = Bukkit.createInventory(null, Calculator.formToLine(this.items.size()) * 9, title);
		for (int i = 0; i < this.items.size(); i++) {
			this.inv.setItem(i, this.items.get(i));
		}
	}
	
	/**
	 * Resets this ChestMenu to a Point BEFORE the User interacted with it
	 */ 
	public void reset(boolean update) {
		if (update) this.inv.clear();
		else this.inv = Bukkit.createInventory(null, Calculator.formToLine(this.items.size()) * 9, title);
		for (int i = 0; i < this.items.size(); i++) {
			this.inv.setItem(i, this.items.get(i));
		}
	}
	
	/**
	 * Modifies an ItemStack in an ALREADY OPENED ChestMenu
	 *
	 * @param  slot The Slot of the Item which will be replaced
	 * @param  item The new Item
	 */ 
	public void replaceExistingItem(int slot, ItemStack item) {
		setup();
		this.inv.setItem(slot, item);
	}
	
	/**
	 * Opens this Menu for the specified Player/s
	 * 
	 * @param players The Players who will see this Menu
	 */ 
	public void open(Player... players) {
		setup();
		for (Player p: players) {
			p.openInventory(this.inv);
			Maps.getInstance().menus.put(p.getUniqueId(), this);
			if (open != null) open.onOpen(p);
		}
	}
	
	/**
	 * Returns the MenuClickHandler which was registered for the specified Slot
	 *
	 * @param  slot The Slot in the Inventory
	 * @return      The MenuClickHandler registered for the specified Slot
	 */ 
	public MenuClickHandler getMenuClickHandler(int slot) {
		return handlers.get(slot);
	}
	
	/**
	 * Returns the registered MenuCloseHandler
	 *
	 * @return      The registered MenuCloseHandler
	 */ 
	public MenuCloseHandler getMenuCloseHandler() {
		return close;
	}
	
	/**
	 * Returns the registered MenuOpeningHandler
	 *
	 * @return      The registered MenuOpeningHandler
	 */ 
	public MenuOpeningHandler getMenuOpeningHandler() {
		return open;
	}

	/**
	 * Returns the registered MenuClickHandler
	 * for Player Inventories
	 *
	 * @return      The registered MenuClickHandler
	 */ 
	public MenuClickHandler getPlayerInventoryClickHandler() {
		return playerclick;
	}
	
	/**
	 * Converts this ChestMenu Instance into a
	 * normal Inventory
	 *
	 * @return      The converted Inventory
	 */ 
	public Inventory toInventory() {
		return this.inv;
	}
	
	public interface MenuClickHandler {
		public boolean onClick(Player p, int slot, ItemStack item, ClickAction action);
	}
	
	public interface AdvancedMenuClickHandler extends MenuClickHandler {
		public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action);
	}
	
	public interface MenuOpeningHandler {
		public void onOpen(Player p);
	}
	
	public interface MenuCloseHandler {
		public void onClose(Player p);
	}
}