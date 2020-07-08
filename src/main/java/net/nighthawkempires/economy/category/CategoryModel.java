package net.nighthawkempires.economy.category;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.FJsonSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.core.util.ItemUtil;
import net.nighthawkempires.economy.EconomyPlugin;
import net.nighthawkempires.economy.commands.EconomyCommand;
import net.nighthawkempires.economy.data.CategoryData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

import static net.nighthawkempires.economy.EconomyPlugin.*;
import static org.bukkit.ChatColor.*;

public class CategoryModel implements Model {

    private String key;

    private Material displayItem;
    private String displayName;

    private List<CategoryItem> items;

    public CategoryModel(String key) {
        this.key = key;

        this.displayItem = Material.GRASS_BLOCK;
        this.displayName = "&3New Category";

        this.items = Lists.newArrayList();
    }

    public CategoryModel(String key, DataSection data) {
        this.key = key;

        this.displayItem = Material.valueOf(data.getString("display_item").toUpperCase());
        this.displayName =  data.getString("display_name");

        this.items = Lists.newArrayList();
        for (Map<String, Object> itemMap : data.getMapList("items")) {
            this.items.add(new CategoryItem(new FJsonSection(itemMap)));
        }
    }

    public String getDisplayName() {
        return translateAlternateColorCodes('&', this.displayName);
    }

    public int getTotalInvPages() {
        return (int) Math.ceil((double) this.items.size() / 36);
    }

    public ItemStack getCategoryItem() {
        ItemStack itemStack = new ItemStack(this.displayItem);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getDisplayName());
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public CategoryItem getItem(int page, int slot) {
        int start = 36 * (page - 1);

        return this.items.get(start + slot);
    }

    public Inventory getPage(int page) {
        Inventory inventory = Bukkit.createInventory(null, 9*6, getDisplayName() + DARK_GRAY + " - Page " + page);
        int start = 36 * (page - 1);
        int finish;

        if (start + 36 > this.items.size()) {
            finish = items.size();
        } else {
            finish = start + 36;
        }

        for (int i = start; i < finish; i++) {
            inventory.addItem(this.items.get(i).toListingItemStack());
        }

        inventory.setItem(45, ItemUtil.getItemStack(Material.BOOK, BLUE + "Categories"));

        if (hasPreviousPage(page)) {
            inventory.setItem(46, ItemUtil.createSkull(RED + "Previous Page", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"));
        }

        if (hasNextPage(page)) {
            inventory.setItem(52, ItemUtil.createSkull(GREEN + "Next Page", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ=="));
        }

        inventory.setItem(53, ItemUtil.getItemStack(Material.BARRIER, DARK_RED + "Exit"));

        getInventoryData().categoryDataMap.put(inventory, new CategoryData(this, page));
        return inventory;
    }

    public boolean hasNextPage(int currentPage) {
        if (currentPage == getTotalInvPages()) return false;
        if (currentPage < getTotalInvPages()) return true;
        return false;
    }

    public boolean hasPreviousPage(int currentPage) {
        if (currentPage == 1) return false;
        if (currentPage > 1) return true;
        return false;
    }

    public void addItem(CategoryItem categoryItem) {
        this.items.add(categoryItem);
        getCategoryRegistry().register(this);
    }

    public String getKey() {
        return this.key.toLowerCase();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("display_item", this.displayItem.name());
        map.put("display_name", this.displayName);

        List<Map<String, Object>> itemList = Lists.newArrayList();
        for (CategoryItem categoryItem : this.items) {
            itemList.add(categoryItem.serialize());
        }
        map.put("items", itemList);
        return map;
    }
}
