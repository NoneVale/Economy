package net.nighthawkempires.economy.category;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.FJsonSection;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.core.user.UserModel;
import net.nighthawkempires.core.util.ItemUtil;
import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.nighthawkempires.economy.EconomyPlugin.*;
import static org.bukkit.ChatColor.*;

public class CategoryItem {

    private Material itemType;

    private boolean isBuyable;
    private boolean isSellable;

    private double buyPrice;
    private double sellPrice;

    private HashMap<Enchantment, Integer> enchantments;

    private List<String> lore;
    private String displayName;

    private PotionType potionType;
    private boolean potionExtended;
    private boolean potionUpgraded;

    public CategoryItem(DataSection data) {
        this.itemType = Material.valueOf(data.getString("item_type"));

        this.isBuyable = data.containsKey("buy_price");
        this.isSellable = data.containsKey("sell_price");
        if (this.isBuyable)
            this.buyPrice = data.getDouble("buy_price");
        if (this.isSellable)
            this.sellPrice = data.getDouble("sell_price");

        this.enchantments = Maps.newHashMap();
        if (data.containsKey("enchantments")) {
            DataSection enchantmentSection = data.getSectionNullable("enchantments");
            for (String name : enchantmentSection.keySet()) {
                this.enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(name)), enchantmentSection.getInt(name));
            }
        }
        
        if (data.containsKey("potion_type")) {
            this.potionType = PotionType.valueOf(data.getString("potion_type"));
            if (data.containsKey("potion_extended"))
                this.potionExtended = data.getBoolean("potion_extended");
            else 
                this.potionExtended = false;
            if (data.containsKey("potion_upgraded"))
                this.potionUpgraded = data.getBoolean("potion_upgraded");
            else 
                this.potionUpgraded = false;
        }

        if (data.containsKey("lore")) {
            this.lore = data.getStringList("lore");
        }

        if (data.containsKey("display-name")) {
            this.displayName = data.getString("display_name");
        }
    }
    
    public Material getItemType() {
        return this.itemType;
    }

    public double getBuyPrice() {
        return this.buyPrice;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public boolean isPotionExtended() {
        return potionExtended;
    }

    public boolean isPotionUpgraded() {
        return potionUpgraded;
    }

    public Inventory openCheckoutPage(Player player) {
        UserModel userModel = CorePlugin.getUserRegistry().getUser(player.getUniqueId());

        ItemStack itemStack = toListingItemStack();

        Inventory inventory = Bukkit.createInventory(null, 54, "Item Shop - " + enumName(this.itemType.name()));

        int maxStackSize = itemStack.getMaxStackSize();
        if (maxStackSize == 1) {
            if (this.isBuyable)
                inventory.setItem(13, buy());
            inventory.setItem(22, toItemStack());
            if (this.isSellable)
                inventory.setItem(31, sell());
        } else if (maxStackSize == 16) {
            if (this.isBuyable) {
                inventory.setItem(11, buy());
                inventory.setItem(12, buy(2));
                inventory.setItem(13, buy(4));
                inventory.setItem(14, buy(8));
                inventory.setItem(15, buy(16));
            }

            inventory.setItem(20, toItemStack());
            inventory.setItem(21, toItemStack(2));
            inventory.setItem(22, toItemStack(4));
            inventory.setItem(23, toItemStack(8));
            inventory.setItem(24, toItemStack(16));

            if (this.isSellable) {
                inventory.setItem(29, sell());
                inventory.setItem(30, sell(2));
                inventory.setItem(31, sell(4));
                inventory.setItem(32, sell(8));
                inventory.setItem(33, sell(16));
                if (getAmountOfItems(player, itemStack) > 0)
                    inventory.setItem(40, sell(getAmountOfItems(player, itemStack)));
            }
        } else {
            if (this.isBuyable) {
                inventory.setItem(11, buy());
                inventory.setItem(12, buy(8));
                inventory.setItem(13, buy(16));
                inventory.setItem(14, buy(32));
                inventory.setItem(15, buy(64));
            }

            inventory.setItem(20, toItemStack());
            inventory.setItem(21, toItemStack(8));
            inventory.setItem(22, toItemStack(16));
            inventory.setItem(23, toItemStack(32));
            inventory.setItem(24, toItemStack(64));

            if (this.isSellable) {
                inventory.setItem(29, sell());
                inventory.setItem(30, sell(8));
                inventory.setItem(31, sell(16));
                inventory.setItem(32, sell(32));
                inventory.setItem(33, sell(64));
                if (getAmountOfItems(player, itemStack) > 0)
                    inventory.setItem(40, sell(getAmountOfItems(player, itemStack)));
            }
        }

        inventory.setItem(45, ItemUtil.getItemStack(Material.BOOK, BLUE + "Categories"));
        inventory.setItem(26, playerBalance(player));
        inventory.setItem(53, ItemUtil.getItemStack(Material.BARRIER, DARK_RED + "Exit"));

        getInventoryData().checkoutDataMap.put(inventory, this);
        return inventory;
    }

    public ItemStack buy() {
        return buy(1);
    }

    public ItemStack buy(int amount) {
        ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(RED + "Buy " + amount);
        itemMeta.setLore(Lists.newArrayList(
                GRAY + "Buy " + GOLD + amount + GRAY + " for " + GREEN + "$" + YELLOW + (buyPrice * amount)
        ));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack sell() {
        return sell(1);
    }

    public ItemStack sell(int amount) {
        ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(RED + "Sell " + amount);
        itemMeta.setLore(Lists.newArrayList(
                GRAY + "Sell " + GOLD + amount + GRAY + " for " + GREEN + "$" + YELLOW + (sellPrice * amount)
        ));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack toItemStack()  {
        return toItemStack(1);
    }

    public ItemStack toItemStack(int amount) {
        ItemStack itemStack = new ItemStack(this.itemType, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (this.lore != null && !this.lore.isEmpty()) {
            itemMeta.setLore(this.lore);
        }

        if (this.displayName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.displayName));
        }

        itemStack.setItemMeta(itemMeta);

        if (potionType != null && (this.itemType == Material.POTION
                || this.itemType == Material.SPLASH_POTION || this.itemType == Material.LINGERING_POTION)) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setBasePotionData(new PotionData(this.potionType,
                    this.potionType.isExtendable() && this.potionExtended && !this.potionUpgraded,
                    this.potionType.isUpgradeable() && this.potionUpgraded && !this.potionExtended));

            itemStack.setItemMeta(potionMeta);
        }

        if (!this.enchantments.isEmpty()) {
            itemStack.addUnsafeEnchantments(this.enchantments);
        }

        return itemStack;
    }

    public ItemStack toListingItemStack() {
        return toListingItemStack(1);
    }

    public ItemStack toListingItemStack(int amount) {
        ItemStack itemStack = new ItemStack(this.itemType, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Lists.newArrayList();

        if (this.isBuyable)
            lore.add(DARK_GRAY + "Buy" + GRAY + ": " + GREEN + "$" + YELLOW + (this.buyPrice * amount));

        if (this.isSellable)
            lore.add(DARK_GRAY + "Sell" + GRAY + ": " + GREEN + "$" + YELLOW + (this.sellPrice * amount));

        if (this.lore != null && !this.lore.isEmpty()) {
            lore.addAll(this.lore);
        }

        if (this.displayName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.displayName));
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        if (potionType != null && (this.itemType == Material.POTION
                || this.itemType == Material.SPLASH_POTION || this.itemType == Material.LINGERING_POTION)) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setBasePotionData(new PotionData(this.potionType,
                    this.potionType.isExtendable() && this.potionExtended && !this.potionUpgraded,
                    this.potionType.isUpgradeable() && this.potionUpgraded && !this.potionExtended));

            itemStack.setItemMeta(potionMeta);
        }

        if (!this.enchantments.isEmpty()) {
            itemStack.addUnsafeEnchantments(this.enchantments);
        }

        return itemStack;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("item_type", this.itemType.name());

        if (this.isBuyable)
            map.put("buy_price", this.buyPrice);
        if (this.isSellable)
            map.put("sell_price", this.sellPrice);

        if (potionType != null && (this.itemType == Material.POTION
                || this.itemType == Material.SPLASH_POTION || this.itemType == Material.LINGERING_POTION)) {
            map.put("potion_type", this.potionType.name());
            if (this.potionExtended)
                map.put("potion_extended", true);
            if (this.potionUpgraded)
                map.put("potion_upgraded", true);
        }

        if (!this.enchantments.isEmpty()) {
            Map<String, Object> enchantmentMap = Maps.newHashMap();
            for (Enchantment enchantment : this.enchantments.keySet()) {
                enchantmentMap.put(enchantment.getKey().getKey(), this.enchantments.get(enchantment));
            }

            map.put("enchantments", enchantmentMap);
        }

        if (this.lore != null && !this.lore.isEmpty()) {
            map.put("lore", this.lore);
        }

        if (this.displayName != null) {
            map.put("display_name", this.displayName);
        }

        return map;
    }

    public static CategoryItem getCategoryItem(Material material, double buyPrice, double sellPrice) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("item_type", material.name());
        map.put("buy_price", buyPrice);
        map.put("sell_price", sellPrice);

        return new CategoryItem(new FJsonSection(map));
    }

    public static CategoryItem getCategoryItem(Material material, double buyPrice, double sellPrice, Map<Enchantment, Integer> enchantments) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("item_type", material.name());
        map.put("buy_price", buyPrice);
        map.put("sell_price", sellPrice);

        Map<String, Object> enchantmentMap = Maps.newHashMap();
        for (Enchantment enchantment : enchantments.keySet()) {
            enchantmentMap.put(enchantment.getKey().getKey(), enchantments.get(enchantment));
        }

        map.put("enchantments", enchantmentMap);

        return new CategoryItem(new FJsonSection(map));
    }

    public static CategoryItem getCategoryItem(Material itemType, double buyPrice, double sellPrice, PotionType potionType, boolean potionExtended, boolean potionUpgraded) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("item_type", itemType.name());
        map.put("buy_price", buyPrice);
        map.put("sell_price", sellPrice);

        if (potionType != null && (itemType == Material.POTION
                || itemType == Material.SPLASH_POTION || itemType == Material.LINGERING_POTION)) {
            map.put("potion_type", potionType.name());
            if (potionExtended)
                map.put("potion_extended", true);
            if (potionUpgraded)
                map.put("potion_upgraded", true);
        }
        return new CategoryItem(new FJsonSection(map));
    }

    public String enumName(String s) {
        if (s.contains("_")) {
            String[] split = s.split("_");

            StringBuilder matName = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                matName.append(enumName(split[i]));

                if (i < split.length - 1) {
                    matName.append(" ");
                }
            }

            return matName.toString();
        }

        return s.toUpperCase().substring(0, 1) + s.substring(1).toLowerCase();
    }

    public int getAmountOfItems(Player player, ItemStack itemStack) {
        int amount = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null) {
                if (itemIsSellable(content)) {
                    if (content.getType() == itemStack.getType())
                        amount += content.getAmount();
                }
            }
        }

        return amount;
    }

    private boolean itemIsSellable(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            return ((Damageable) itemMeta).getDamage() == 0;
        }
        return true;
    }

    private ItemStack playerBalance(Player player) {
        UserModel userModel = CorePlugin.getUserRegistry().getUser(player.getUniqueId());
        ServerType serverType = CorePlugin.getConfigg().getServerType();

        double playerBalance = userModel.getServerBalance(serverType);

        ItemStack itemStack = new ItemStack(Material.EMERALD);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(DARK_GREEN + "Balance");
        itemMeta.setLore(Lists.newArrayList(
                GRAY + "Your balance is " + GREEN + "$" + YELLOW + StringUtil.formatBalance(playerBalance)
        ));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
