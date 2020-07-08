package net.nighthawkempires.economy.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.economy.category.CategoryItem;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

public class InventoryData {

    public List<Inventory> categoryInventoryList;

    public Map<Inventory, CategoryData> categoryDataMap;
    public Map<Inventory, CategoryItem> checkoutDataMap;

    public InventoryData() {
        this.categoryInventoryList = Lists.newArrayList();

        this.categoryDataMap = Maps.newHashMap();
        this.checkoutDataMap = Maps.newHashMap();
    }
}