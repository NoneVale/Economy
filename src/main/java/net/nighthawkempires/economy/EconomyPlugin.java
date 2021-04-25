package net.nighthawkempires.economy;

import com.google.common.collect.Maps;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.economy.category.CategoryItem;
import net.nighthawkempires.economy.category.CategoryModel;
import net.nighthawkempires.economy.category.registry.CategoryRegistry;
import net.nighthawkempires.economy.category.registry.FCategoryRegistry;
import net.nighthawkempires.economy.commands.*;
import net.nighthawkempires.economy.data.InventoryData;
import net.nighthawkempires.economy.listeners.InventoryListener;
import net.nighthawkempires.economy.tabcompleters.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class EconomyPlugin extends JavaPlugin {

    private static CategoryRegistry categoryRegistry;

    private static InventoryData inventoryData;

    private static boolean regionsEnabled;

    public void onEnable() {
        if (!CorePlugin.getConfigg().isEconomyBased()) {
            getLogger().warning("Shutting down due to server not being economy based.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        categoryRegistry = new FCategoryRegistry();
        categoryRegistry.loadAllFromDb();

        inventoryData = new InventoryData();

        registerCommands();
        registerTabCompleters();
        registerListeners();

        regionsEnabled = Bukkit.getPluginManager().isPluginEnabled("Regions");
    }

    public void onDisable() {

    }

    public void registerCommands() {
        this.getCommand("balance").setExecutor(new BalanceCommand());
        this.getCommand("balancetop").setExecutor(new BalanceTopCommand());
        this.getCommand("economy").setExecutor(new EconomyCommand());
        this.getCommand("pay").setExecutor(new PayCommand());
        this.getCommand("shop").setExecutor(new ShopCommand());
    }

    public void registerTabCompleters() {
        this.getCommand("balance").setTabCompleter(new BalanceTabCompleter());
        this.getCommand("balancetop").setTabCompleter(new BalanceTopTabCompleter());
        this.getCommand("economy").setTabCompleter(new EconomyTabCompleter());
        this.getCommand("pay").setTabCompleter(new PayTabCompleter());
        this.getCommand("shop").setTabCompleter(new ShopTabCompleter());
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryListener(), this);
    }

    public static CategoryRegistry getCategoryRegistry() {
        return categoryRegistry;
    }

    public static InventoryData getInventoryData() {
        return inventoryData;
    }

    public static boolean isRegionsEnabled() {
        return regionsEnabled;
    }
}
