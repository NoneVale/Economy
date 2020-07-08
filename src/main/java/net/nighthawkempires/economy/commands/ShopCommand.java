package net.nighthawkempires.economy.commands;

import com.google.common.collect.Maps;
import net.nighthawkempires.economy.category.CategoryItem;
import net.nighthawkempires.economy.category.CategoryModel;
import net.nighthawkempires.regions.RegionsPlugin;
import net.nighthawkempires.regions.region.RegionFlag;
import net.nighthawkempires.regions.region.RegionModel;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.Map;

import static net.nighthawkempires.core.CorePlugin.getCommandManager;
import static net.nighthawkempires.core.CorePlugin.getMessages;
import static net.nighthawkempires.core.lang.Messages.*;
import static net.nighthawkempires.economy.EconomyPlugin.*;
import static org.bukkit.ChatColor.*;

public class ShopCommand implements CommandExecutor {

    public ShopCommand() {
        getCommandManager().registerCommands("shop", new String[] {
                "ne.shop"
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.shop") && !player.hasPermission("ne.shop.admin")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    if (isRegionsEnabled()) {
                        RegionModel region = RegionsPlugin.getRegionRegistry().getObeyRegion(player.getLocation());

                        if (region != null) {
                            if (region.getFlagResult(RegionFlag.MARKET) == RegionFlag.Result.ALLOW) {
                                player.openInventory(getCategoryRegistry().getCategoryInventory());
                                return true;
                            } else {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You must be in a market region to do this."));
                                return true;
                            }
                        } else {
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You must be in a market region to do this."));
                            return true;
                        }
                    } else {
                        player.openInventory(getCategoryRegistry().getCategoryInventory());
                        return true;
                    }
                case 1:
                    String name = args[0].toLowerCase();

                    if (!getCategoryRegistry().categoryExists(name)) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but there isn't a shop category that exists with that name."));
                        return true;
                    }

                    CategoryModel categoryModel = getCategoryRegistry().getCategory(name);

                    if (isRegionsEnabled()) {
                        RegionModel region = RegionsPlugin.getRegionRegistry().getObeyRegion(player.getLocation());

                        if (region != null) {
                            if (region.getFlagResult(RegionFlag.MARKET) == RegionFlag.Result.ALLOW) {
                                player.openInventory(categoryModel.getPage(1));
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You have opened category " + YELLOW + categoryModel.getKey() + GRAY + "."));
                                return true;
                            } else {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You must be in a market region to do this."));
                                return true;
                            }
                        } else {
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You must be in a market region to do this."));
                            return true;
                        }
                    } else {
                        player.openInventory(categoryModel.getPage(1));
                        player.sendMessage(getMessages().getChatMessage(GRAY + "You have opened category " + YELLOW + categoryModel.getKey() + GRAY + "."));
                        return true;
                    }
                case 2:
                    if (args[0].toLowerCase().equals("create")) {
                        if (!player.hasPermission("ne.shop.admin")) {
                            player.sendMessage(getMessages().getChatTag(NO_PERMS));
                            return true;
                        }

                        name = args[1];

                        if (getCategoryRegistry().categoryExists(name)) {
                            player.sendMessage(getMessages().getChatMessage(GRAY + "A category with that name already exists."));
                            return true;
                        }

                        categoryModel = getCategoryRegistry().getCategory(name);

                        categoryModel.addItem(CategoryItem.getCategoryItem(Material.STONE, 5, 2.5));
                        categoryModel.addItem(CategoryItem.getCategoryItem(Material.OAK_LOG, 5, 2.5));
                        categoryModel.addItem(CategoryItem.getCategoryItem(Material.GRASS_BLOCK, 10, 2.5));
                        Map<Enchantment, Integer> enchantments = Maps.newHashMap();
                        enchantments.put(Enchantment.DIG_SPEED, 4);
                        categoryModel.addItem(CategoryItem.getCategoryItem(Material.IRON_SHOVEL, 150, 5, enchantments));
                        categoryModel.addItem(CategoryItem.getCategoryItem(Material.SPLASH_POTION, 250, 0, PotionType.SPEED, false, true));
                        player.sendMessage(getMessages().getChatMessage(GRAY + "You have created category " + YELLOW + categoryModel.getKey() + GRAY + "."));
                        return true;
                    } else {
                        player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                        return true;
                    }
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}