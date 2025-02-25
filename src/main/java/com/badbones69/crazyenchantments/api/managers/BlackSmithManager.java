package com.badbones69.crazyenchantments.api.managers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class BlackSmithManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private ItemStack denyBarrier;
    private ItemStack redGlass;
    private ItemStack blueGlass;
    private ItemStack grayGlass;
    private String menuName;
    private String foundString;
    private Currency currency;
    private int bookUpgrade;
    private int levelUp;
    private int addEnchantment;
    private boolean maxEnchantments;
    
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        denyBarrier = new ItemBuilder()
        .setMaterial(Material.BARRIER)
        .setName(config.getString("Settings.BlackSmith.Results.None", "&c&lNo Results"))
        .setLore(config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore"))
        .build();
        redGlass = new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE").setName(" ").build();
        grayGlass = new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE").setName(" ").build();
        blueGlass = new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE").build();
        menuName = starter.color(config.getString("Settings.BlackSmith.GUIName", "&7&lThe &b&lBlack &9&lSmith"));
        foundString = config.getString("Settings.BlackSmith.Results.Found", "&c&lCost: &6&l%cost%XP");
        currency = Currency.getCurrency(config.getString("Settings.BlackSmith.Transaction.Currency", "XP_Level"));
        bookUpgrade = config.getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade", 5);
        levelUp = config.getInt("Settings.BlackSmith.Transaction.Costs.Power-Up", 5);
        addEnchantment = config.getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment", 3);
        maxEnchantments = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle");
    }
    
    public ItemStack getDenyBarrier() {
        return denyBarrier;
    }
    
    public ItemStack getRedGlass() {
        return redGlass;
    }
    
    public ItemStack getGrayGlass() {
        return grayGlass;
    }
    
    public ItemStack getBlueGlass() {
        return blueGlass;
    }
    
    public String getMenuName() {
        return menuName;
    }
    
    public String getFoundString() {
        return foundString;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public int getBookUpgrade() {
        return bookUpgrade;
    }
    
    public int getLevelUp() {
        return levelUp;
    }
    
    public int getAddEnchantment() {
        return addEnchantment;
    }
    
    public boolean useMaxEnchantments() {
        return maxEnchantments;
    }
}