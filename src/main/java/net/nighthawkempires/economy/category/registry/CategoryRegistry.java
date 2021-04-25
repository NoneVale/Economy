package net.nighthawkempires.economy.category.registry;

import com.google.common.collect.ImmutableList;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.economy.EconomyPlugin;
import net.nighthawkempires.economy.category.CategoryModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

import static net.nighthawkempires.economy.EconomyPlugin.*;
import static org.bukkit.ChatColor.*;

public interface CategoryRegistry extends Registry<CategoryModel> {

    default CategoryModel fromDataSection(String stringKey, DataSection data) {
        return new CategoryModel(stringKey, data);
    }

    default CategoryModel getCategory(String name) {
        return fromKey(name).orElseGet(() -> register(new CategoryModel(name)));
    }

    default Inventory getCategoryInventory() {
        int size = 9;
        int categories = getCategories().size();
        while (size < categories) {
            size = size + 9;
        }

        Inventory inventory = Bukkit.createInventory(null, size, BLUE + "Shop Categories");

        for (CategoryModel categoryModel : getRegisteredData().values()) {
            inventory.addItem(categoryModel.getCategoryItem());
        }

        for (int i = 0; i < categories; i++) {
            inventory.setItem(i, getCategories().get(i).getCategoryItem());
        }

        getInventoryData().categoryInventoryList.add(inventory);
        return inventory;
    }

    default ImmutableList<CategoryModel> getCategories() {
        return ImmutableList.copyOf(getRegisteredData().values());
    }

    @Deprecated
    Map<String, CategoryModel> getRegisteredData();

    default Map<String, CategoryModel> getCategoriess() {
        return loadAllFromDb();
    }

    default boolean categoryExists(String name) {
        return fromKey(name).isPresent();
    }
}
