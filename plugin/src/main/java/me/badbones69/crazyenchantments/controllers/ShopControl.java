package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.events.BuyBookEvent;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.api.objects.LostBook;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopControl implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	public static void openGUI(Player player) {
		FileConfiguration config = Files.CONFIG.getFile();
		int size = config.getInt("Settings.GUISize");
		Inventory inv = Bukkit.createInventory(null, size, Methods.color(config.getString("Settings.InvName")));
		if(config.contains("Settings.GUICustomization")) {
			for(String custom : config.getStringList("Settings.GUICustomization")) {
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<>();
				String[] b = custom.split(", ");
				for(String i : b) {
					if(i.contains("Item:")) {
						i = i.replace("Item:", "");
						item = i;
					}
					if(i.contains("Name:")) {
						i = i.replace("Name:", "");
						for(Currency c : Currency.values()) {
							i = i.replaceAll("%" + c.getName().toLowerCase() + "%", CurrencyAPI.getCurrency(player, c) + "");
						}
						name = i;
					}
					if(i.contains("Slot:")) {
						i = i.replace("Slot:", "");
						slot = Integer.parseInt(i);
					}
					if(i.contains("Lore:")) {
						i = i.replace("Lore:", "");
						String[] d = i.split(",");
						for(String l : d) {
							for(Currency c : Currency.values()) {
								l = l.replaceAll("%" + c.getName().toLowerCase() + "%", CurrencyAPI.getCurrency(player, c) + "");
							}
							lore.add(l);
						}
					}
				}
				if(slot > size || slot <= 0) {
					continue;
				}
				slot--;
				inv.setItem(slot, new ItemBuilder().setMaterial(item).setName(name).setLore(lore).build());
			}
		}
		for(Category category : ce.getCategories()) {
			if(category.isInGUI()) {
				if(category.getSlot() > size) {
					continue;
				}
				inv.setItem(category.getSlot(), category.getDisplayItem().build());
			}
			LostBook lostBook = category.getLostBook();
			if(lostBook.isInGUI()) {
				if(lostBook.getSlot() > size) {
					continue;
				}
				inv.setItem(lostBook.getSlot(), lostBook.getDisplayItem().build());
			}
		}
		ArrayList<String> options = new ArrayList<>();
		if(ce.isGkitzEnabled()) options.add("GKitz");//Only adds if the gkit option is enabled.
		options.add("BlackSmith");
		options.add("Tinker");
		options.add("Info");
		for(String op : options) {
			if(config.contains("Settings." + op)) {
				if(config.getBoolean("Settings." + op + ".InGUI")) {
					String name = config.getString("Settings." + op + ".Name");
					String id = config.getString("Settings." + op + ".Item");
					List<String> lore = config.getStringList("Settings." + op + ".Lore");
					int slot = config.getInt("Settings." + op + ".Slot") - 1;
					boolean glowing = false;
					if(config.contains("Settings." + op + ".Glowing")) {
						glowing = config.getBoolean("Settings." + op + ".Glowing");
					}
					if(slot > size) {
						continue;
					}
					inv.setItem(slot, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
				}
			}
		}
		options.clear();
		options.add("ProtectionCrystal");
		options.add("Dust.SuccessDust");
		options.add("Dust.DestroyDust");
		options.add("Scrambler");
		for(String op : options) {
			if(config.contains("Settings." + op)) {
				if(config.getBoolean("Settings." + op + ".InGUI")) {
					String name = config.getString("Settings." + op + ".GUIName");
					String id = config.getString("Settings." + op + ".Item");
					List<String> lore = config.getStringList("Settings." + op + ".GUILore");
					int slot = config.getInt("Settings." + op + ".Slot") - 1;
					boolean glowing = false;
					if(config.contains("Settings." + op + ".Glowing")) {
						glowing = config.getBoolean("Settings." + op + ".Glowing");
					}
					if(slot > size) {
						continue;
					}
					inv.setItem(slot, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
				}
			}
		}
		options.clear();
		options.add("BlackScroll");
		options.add("WhiteScroll");
		options.add("TransmogScroll");
		for(String op : options) {
			if(config.contains("Settings." + op)) {
				if(config.getBoolean("Settings." + op + ".InGUI")) {
					String name = config.getString("Settings." + op + ".GUIName");
					String id = config.getString("Settings." + op + ".Item");
					List<String> lore = config.getStringList("Settings." + op + ".Lore");
					int slot = config.getInt("Settings." + op + ".Slot") - 1;
					boolean glowing = false;
					if(config.contains("Settings." + op + ".Glowing")) {
						glowing = config.getBoolean("Settings." + op + ".Glowing");
					}
					if(slot > size) {
						continue;
					}
					inv.setItem(slot, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
				}
			}
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		FileConfiguration config = Files.CONFIG.getFile();
		if(inv != null) {
			if(e.getView().getTitle().equals(Methods.color(config.getString("Settings.InvName")))) {
				e.setCancelled(true);
				if(e.getRawSlot() >= inv.getSize()) return;
				if(item == null) return;
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getDisplayName();
						for(Category category : ce.getCategories()) {
							if(category.isInGUI()) {
								if(item.isSimilar(category.getDisplayItem().build())) {
									if(Methods.isInventoryFull(player)) {
										player.sendMessage(Messages.INVENTORY_FULL.getMessage());
										return;
									}
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(category.getCurrency() != null) {
											if(CurrencyAPI.canBuy(player, category)) {
												CurrencyAPI.takeCurrency(player, category);
											}else {
												String needed = (category.getCost() - CurrencyAPI.getCurrency(player, category.getCurrency())) + "";
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%Money_Needed%", needed);
												placeholders.put("%XP%", needed);
												switch(category.getCurrency()) {
													case VAULT:
														player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
														break;
													case XP_LEVEL:
														player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
														break;
													case XP_TOTAL:
														player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
														break;
												}
												return;
											}
										}
									}
									CEBook book = ce.getRandomEnchantmentBook(category);
									if(book != null) {
										BuyBookEvent event = new BuyBookEvent(ce.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
										Bukkit.getPluginManager().callEvent(event);
										player.getInventory().addItem(book.buildBook());
									}else {
										player.sendMessage(Methods.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
									}
									return;
								}
							}
							LostBook lostBook = category.getLostBook();
							if(lostBook.isInGUI()) {
								if(item.isSimilar(lostBook.getDisplayItem().build())) {
									if(Methods.isInventoryFull(player)) {
										player.sendMessage(Messages.INVENTORY_FULL.getMessage());
										return;
									}
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(lostBook.getCurrency() != null) {
											if(CurrencyAPI.canBuy(player, lostBook)) {
												CurrencyAPI.takeCurrency(player, lostBook);
											}else {
												String needed = (lostBook.getCost() - CurrencyAPI.getCurrency(player, lostBook.getCurrency())) + "";
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%Money_Needed%", needed);
												placeholders.put("%XP%", needed);
												switch(lostBook.getCurrency()) {
													case VAULT:
														player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
														break;
													case XP_LEVEL:
														player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
														break;
													case XP_TOTAL:
														player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
														break;
												}
												return;
											}
										}
									}
									player.getInventory().addItem(lostBook.getLostBook(category).build());
									return;
								}
							}
						}
						List<String> options = new ArrayList<>();
						options.add("BlackScroll");
						options.add("WhiteScroll");
						options.add("TransmogScroll");
						options.add("ProtectionCrystal");
						options.add("Scrambler");
						for(String o : options) {
							if(name.equalsIgnoreCase(Methods.color(config.getString("Settings." + o + ".GUIName")))) {
								if(Methods.isInventoryFull(player)) {
									player.sendMessage(Messages.INVENTORY_FULL.getMessage());
									return;
								}
								if(player.getGameMode() != GameMode.CREATIVE) {
									if(Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))) {
										Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
										int cost = config.getInt("Settings.Costs." + o + ".Cost");
										if(CurrencyAPI.canBuy(player, currency, cost)) {
											CurrencyAPI.takeCurrency(player, currency, cost);
										}else {
											String needed = (cost - CurrencyAPI.getCurrency(player, currency)) + "";
											if(currency != null) {
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%Money_Needed%", needed);
												placeholders.put("%XP%", needed);
												switch(currency) {
													case VAULT:
														player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
														break;
													case XP_LEVEL:
														player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
														break;
													case XP_TOTAL:
														player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
														break;
												}
											}
											return;
										}
									}
								}
								switch(o) {
									case "BlackScroll":
										player.getInventory().addItem(Scrolls.BlACK_SCROLL.getScroll());
										break;
									case "WhiteScroll":
										player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
										break;
									case "TransmogScroll":
										player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
										break;
									case "ProtectionCrystal":
										player.getInventory().addItem(ProtectionCrystal.getCrystals());
										break;
									case "Scrambler":
										player.getInventory().addItem(Scrambler.getScramblers());
										break;
								}
								return;
							}
						}
						options.clear();
						options.add("DestroyDust");
						options.add("SuccessDust");
						for(String o : options) {
							if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.Dust." + o + ".GUIName")))) {
								if(Methods.isInventoryFull(player)) {
									player.sendMessage(Messages.INVENTORY_FULL.getMessage());
									return;
								}
								if(player.getGameMode() != GameMode.CREATIVE) {
									if(Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))) {
										Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
										int cost = config.getInt("Settings.Costs." + o + ".Cost");
										if(CurrencyAPI.canBuy(player, currency, cost)) {
											CurrencyAPI.takeCurrency(player, currency, cost);
										}else {
											String needed = (cost - CurrencyAPI.getCurrency(player, currency)) + "";
											if(currency != null) {
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%Money_Needed%", needed);
												placeholders.put("%XP%", needed);
												switch(currency) {
													case VAULT:
														player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
														break;
													case XP_LEVEL:
														player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
														break;
													case XP_TOTAL:
														player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
														break;
												}
											}
											return;
										}
									}
								}
								switch(o) {
									case "DestroyDust":
										player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
										break;
									case "SuccessDust":
										player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
										break;
								}
								return;
							}
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.GKitz.Name")))) {
							if(!Methods.hasPermission(player, "gkitz", true)) return;
							GKitzController.openGUI(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.BlackSmith.Name")))) {
							if(!Methods.hasPermission(player, "blacksmith", true)) return;
							BlackSmith.openBlackSmith(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.Tinker.Name")))) {
							if(!Methods.hasPermission(player, "tinker", true)) return;
							Tinkerer.openTinker(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.Info.Name")))) {
							ce.getInfoMenuManager().openInfoMenu(player);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEnchantmentTableClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		if(block != null) {
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(block.getType() == new ItemBuilder().setMaterial("ENCHANTING_TABLE", "ENCHANTMENT_TABLE").getMaterial()) {
					if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Right-Click-Enchantment-Table")) {
						e.setCancelled(true);
						openGUI(player);
					}
				}
			}
		}
	}
	
}