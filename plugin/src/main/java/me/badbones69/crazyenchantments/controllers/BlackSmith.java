package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BlackSmith implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private int mainSlot = 10;
	private int subSlot = 13;
	private ItemStack denyBarrier = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).setLore(Files.CONFIG.getFile().getStringList("Settings.BlackSmith.Results.Not-Found-Lore")).build();
	private ItemStack redGlass = new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build();
	private ItemStack blueGlass = new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:3").build();
	
	public static void openBlackSmith(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(Files.CONFIG.getFile().getString("Settings.BlackSmith.GUIName")));
		List<Integer> other = Arrays.asList(1, 2, 3, 4, 5, 6, 10, 12, 13, 15, 19, 20, 21, 22, 23, 24);
		List<Integer> result = Arrays.asList(7, 8, 9, 16, 18, 25, 26, 27);
		for(int i : other)
			inv.setItem(i - 1, new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:7").setName(" ").build());
		for(int i : result)
			inv.setItem(i - 1, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build());
		ItemStack item = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).build();
		if(Files.CONFIG.getFile().contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
			for(String line : Files.CONFIG.getFile().getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
				item = Methods.addLore(item, line);
			}
		}
		inv.setItem(16, item);
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		List<Integer> result = Arrays.asList(7, 8, 9, 16, 18, 25, 26, 27);
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		FileConfiguration config = Files.CONFIG.getFile();
		if(inv != null) {
			if(e.getView().getTitle().equals(Methods.color(config.getString("Settings.BlackSmith.GUIName")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					int resaultSlot = 16;
					if(!inBlackSmith(e.getRawSlot())) {// Click In Players Inventory
						if(item.getAmount() != 1) return;
						if(ce.hasEnchantments(item) || item.getType() == ce.getEnchantmentBook().getMaterial()) {
							if(item.getType() == ce.getEnchantmentBook().getMaterial()) {//Is a custom enchantment book.
								if(!ce.isEnchantmentBook(item)) {
									return;
								}
							}
							if(inv.getItem(mainSlot) == null) {//Main item slot is empty
								e.setCurrentItem(new ItemStack(Material.AIR));
								inv.setItem(mainSlot, item);//Moves clicked item to main slot
								playClick(player);
								if(inv.getItem(subSlot) != null) {//Sub item slot is not empty
									if(getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) > 0) {//Items are upgradable
										inv.setItem(resultSlot, Methods.addLore(getUpgradedItem(player, inv.getItem(mainSlot), inv.getItem(subSlot)),
										config.getString("Settings.BlackSmith.Results.Found")
										.replaceAll("%Cost%", getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) + "")
										.replaceAll("%cost%", getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) + "")));
										for(int i : result)
											inv.setItem(i - 1, blueGlass);
									}else {//Items are not upgradable
										inv.setItem(resultSlot, denyBarrier);
										for(int i : result)
											inv.setItem(i - 1, redGlass);
									}
								}
							}else {//Main item slot is not empty
								e.setCurrentItem(new ItemStack(Material.AIR));
								if(inv.getItem(subSlot) != null) {//Sub item slot is not empty
									e.setCurrentItem(inv.getItem(subSlot));//Moves sub slot item to clicked items slot
								}
								inv.setItem(subSlot, item);//Moves clicked item to sub slot
								playClick(player);
								if(getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) > 0) {//Items are upgradable
									inv.setItem(resultSlot, Methods.addLore(getUpgradedItem(player, inv.getItem(mainSlot), inv.getItem(subSlot)),
									config.getString("Settings.BlackSmith.Results.Found")
									.replaceAll("%Cost%", getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) + "")
									.replaceAll("%cost%", getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) + "")));
									for(int i : result)
										inv.setItem(i - 1, blueGlass);
								}else {//Items are not upgradable
									inv.setItem(resultSlot, denyBarrier);
									for(int i : result)
										inv.setItem(i - 1, redGlass);
								}
							}
						}
					}else {// Click In the Black Smith
						if(e.getRawSlot() == mainSlot || e.getRawSlot() == subSlot) {//Clicked either the Main slot or Sub slot
							e.setCurrentItem(new ItemStack(Material.AIR));//Sets the clicked slot to air
							if(Methods.isInventoryFull(player)) {//Gives clicked item back to player
								player.getWorld().dropItem(player.getLocation(), item);
							}else {
								player.getInventory().addItem(item);
							}
							inv.setItem(resultSlot, denyBarrier);
							for(int i : result)
								inv.setItem(i - 1, redGlass);
							playClick(player);
						}
						if(e.getRawSlot() == resultSlot) {//Clicks the result item slot
							if(inv.getItem(mainSlot) != null && inv.getItem(subSlot) != null) {//Main and Sub items are not empty
								if(getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot)) > 0) {//Items are upgradeable
									int cost = getUpgradeCost(player, inv.getItem(mainSlot), inv.getItem(subSlot));
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(Currency.isCurrency(config.getString("Settings.BlackSmith.Transaction.Currency"))) {
											Currency currency = Currency.getCurrency(config.getString("Settings.BlackSmith.Transaction.Currency"));
											if(CurrencyAPI.canBuy(player, currency, cost)) {
												CurrencyAPI.takeCurrency(player, currency, cost);
											}else {
												String needed = (cost - CurrencyAPI.getCurrency(player, currency)) + "";
												if(currency != null) {
													HashMap<String, String> placeholders = new HashMap<>();
													placeholders.put("%money_needed%", needed);
													placeholders.put("%xp%", needed);
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
									if(Methods.isInventoryFull(player)) {
										player.getWorld().dropItem(player.getLocation(), getUpgradedItem(player, inv.getItem(mainSlot), inv.getItem(subSlot)));
									}else {
										player.getInventory().addItem(getUpgradedItem(player, inv.getItem(mainSlot), inv.getItem(subSlot)));
									}
									inv.setItem(mainSlot, new ItemStack(Material.AIR));
									inv.setItem(subSlot, new ItemStack(Material.AIR));
									player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
									inv.setItem(resultSlot, denyBarrier);
									for(int i : result)
										inv.setItem(i - 1, redGlass);
								}else {
									player.playSound(player.getLocation(), ce.getSound("ENTITY_VILLAGER_NO", "VILLAGER_NO"), 1, 1);
								}
							}else {
								player.playSound(player.getLocation(), ce.getSound("ENTITY_VILLAGER_NO", "VILLAGER_NO"), 1, 1);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInvClose(final InventoryCloseEvent e) {
		final Inventory inv = e.getInventory();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ce.getPlugin(), () -> {
			if(inv != null) {
				if(e.getView().getTitle().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.BlackSmith.GUIName")))) {
					List<Integer> slots = new ArrayList<>();
					slots.add(mainSlot);
					slots.add(subSlot);
					boolean dead = e.getPlayer().isDead();
					for(int slot : slots) {
						if(inv.getItem(slot) != null) {
							if(inv.getItem(slot).getType() != Material.AIR) {
								if(dead) {
									e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
								}else {
									if(Methods.isInventoryFull(((Player) e.getPlayer()))) {
										e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
									}else {
										e.getPlayer().getInventory().addItem(inv.getItem(slot));
									}
								}
							}
						}
					}
					inv.clear();
				}
			}
		}, 0);
	}
	
	private ItemStack getUpgradedItem(Player player, ItemStack mainItem, ItemStack subItem) {
		ItemStack item = mainItem.clone();
		if(mainItem.getType() == ce.getEnchantmentBookItem().getType() && subItem.getType() == ce.getEnchantmentBookItem().getType()) {
			if(Methods.removeColor(mainItem.getItemMeta().getDisplayName()).equalsIgnoreCase(Methods.removeColor(subItem.getItemMeta().getDisplayName()))) {
				for(CEnchantment en : ce.getRegisteredEnchantments()) {
					if(mainItem.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
						int power = ce.getBookLevel(mainItem, en);
						int max = Files.ENCHANTMENTS.getFile().getInt("Enchantments." + en.getName() + ".MaxPower");
						if(power + 1 <= max) {
							item = ce.getEnchantmentBook().setName(en.getBookColor() + en.getCustomName() + " " + ce.convertLevelString(power + 1)).setLore(mainItem.getItemMeta().getLore()).setGlowing(Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing")).build();
						}
					}
				}
			}
		}
		if(mainItem.getType() != ce.getEnchantmentBookItem().getType() || subItem.getType() != ce.getEnchantmentBookItem().getType()) {
			if(mainItem.getType() == subItem.getType()) {
				HashMap<String, Integer> dupEnchants = new HashMap<>();
				HashMap<String, Integer> newEnchants = new HashMap<>();
				HashMap<String, Integer> higherEnchants = new HashMap<>();
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(mainItem)) {
					if(ce.hasEnchantment(subItem, enchant)) {
						if(ce.getLevel(mainItem, enchant) == ce.getLevel(subItem, enchant)) {
							if(!dupEnchants.containsKey(enchant.getName())) {
								dupEnchants.put(enchant.getName(), ce.getLevel(mainItem, enchant));
							}
						}else {
							if(ce.getLevel(mainItem, enchant) < ce.getLevel(subItem, enchant)) {
								higherEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
							}
						}
					}
				}
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(subItem)) {
					if(!dupEnchants.containsKey(enchant.getName()) && !higherEnchants.containsKey(enchant.getName())) {
						if(!ce.hasEnchantment(mainItem, enchant)) {
							newEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
						}
					}
				}
				for(String enchant : dupEnchants.keySet()) {
					if(ce.getEnchantmentFromName(enchant) != null) {
						int power = dupEnchants.get(enchant);
						int max = ce.getEnchantmentFromName(enchant).getMaxLevel();
						if(power + 1 <= max) {
							item = ce.addEnchantment(item, ce.getEnchantmentFromName(enchant), power + 1);
						}
					}
				}
				int maxEnchants = ce.getPlayerMaxEnchantments(player);
				for(String enchant : newEnchants.keySet()) {
					if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
						if((Methods.getEnchantmentAmount(item) + 1) <= maxEnchants) {
							if(ce.getEnchantmentFromName(enchant) != null) {
								item = ce.addEnchantment(item, ce.getEnchantmentFromName(enchant), newEnchants.get(enchant));
							}
						}
					}
				}
				for(String enchant : higherEnchants.keySet()) {
					if(ce.getEnchantmentFromName(enchant) != null) {
						item = ce.addEnchantment(item, ce.getEnchantmentFromName(enchant), higherEnchants.get(enchant));
					}
				}
			}
		}
		return item;
	}
	
	private int getUpgradeCost(Player player, ItemStack mainItem, ItemStack subItem) {
		int total = 0;
		//Is 2 books
		if(mainItem.getType() == ce.getEnchantmentBookItem().getType() && subItem.getType() == ce.getEnchantmentBookItem().getType()) {
			if(Methods.removeColor(mainItem.getItemMeta().getDisplayName()).equalsIgnoreCase(Methods.removeColor(subItem.getItemMeta().getDisplayName()))) {
				for(CEnchantment en : ce.getRegisteredEnchantments()) {
					if(ce.getEnchantmentBookEnchantment(mainItem) == en) {
						int power = ce.getBookLevel(mainItem, en);
						int max = en.getMaxLevel();
						if(power + 1 <= max) {
							total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade");
						}
					}
				}
			}
		}
		//Is 2 items
		if(mainItem.getType() != ce.getEnchantmentBookItem().getType() || subItem.getType() != ce.getEnchantmentBookItem().getType()) {
			if(mainItem.getType() == subItem.getType()) {
				ItemStack item = mainItem.clone();
				HashMap<String, Integer> dupEnchants = new HashMap<>();
				HashMap<String, Integer> newEnchants = new HashMap<>();
				HashMap<String, Integer> higherEnchants = new HashMap<>();
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(mainItem)) {
					if(ce.hasEnchantment(subItem, enchant)) {
						if(ce.getLevel(mainItem, enchant) == ce.getLevel(subItem, enchant)) {
							if(!dupEnchants.containsKey(enchant.getName())) {
								dupEnchants.put(enchant.getName(), ce.getLevel(mainItem, enchant));
							}
						}else {
							if(ce.getLevel(mainItem, enchant) < ce.getLevel(subItem, enchant)) {
								higherEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
							}
						}
					}
				}
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(subItem)) {
					if(!dupEnchants.containsKey(enchant.getName()) && !higherEnchants.containsKey(enchant.getName())) {
						if(!ce.hasEnchantment(mainItem, enchant)) {
							newEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
						}
					}
				}
				for(String enchant : dupEnchants.keySet()) {
					if(ce.getEnchantmentFromName(enchant) != null) {
						int power = dupEnchants.get(enchant);
						int max = ce.getEnchantmentFromName(enchant).getMaxLevel();
						if(power + 1 <= max) {
							total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
						}
					}
				}
				int maxEnchants = ce.getPlayerMaxEnchantments(player);
				for(int i = 0; i < newEnchants.size(); i++) {
					if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
						if((Methods.getEnchantmentAmount(item) + i + 1) <= maxEnchants) {
							total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment");
						}
					}
				}
				for(int i = 0; i < higherEnchants.size(); i++) {
					total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
				}
			}
		}
		return total;
	}
	
	private boolean inBlackSmith(int slot) {
		//The last slot in the tinker is 54
		return slot < 27;
	}
	
	private void playClick(Player player) {
		player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
	}
	
}
