package net.nighthawkempires.economy.listeners;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.core.user.UserModel;
import net.nighthawkempires.economy.EconomyPlugin;
import net.nighthawkempires.economy.category.CategoryItem;
import net.nighthawkempires.economy.category.CategoryModel;
import net.nighthawkempires.economy.data.CategoryData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.economy.EconomyPlugin.*;
import static org.bukkit.ChatColor.*;

public class InventoryListener implements Listener {

    @EventHandler
    public void onCLick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (getInventoryData().categoryDataMap.containsKey(event.getView().getTopInventory())) {
                if (getInventoryData().categoryDataMap.containsKey(event.getClickedInventory())) {
                    CategoryData data = getInventoryData().categoryDataMap.get(event.getClickedInventory());
                    CategoryModel model = data.getCategoryModel();
                    int page = data.getCurrentPage();

                    if (event.getCurrentItem() != null) {
                        int clickedSlot = event.getSlot();
                        if (clickedSlot >= 0 && clickedSlot <= 35) {
                            event.getWhoClicked().closeInventory();
                            CategoryItem categoryItem = model.getItem(page, clickedSlot);
                            event.getWhoClicked().openInventory(categoryItem.openCheckoutPage(player));
                        } else if (clickedSlot == 45) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().openInventory(getCategoryRegistry().getCategoryInventory());
                        } else if (clickedSlot == 46) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().openInventory(model.getPage(page - 1));
                        } else if (clickedSlot == 52) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().openInventory(model.getPage(page + 1));
                        } else if (clickedSlot == 53) {
                            event.getWhoClicked().closeInventory();
                        }
                    }
                }
                event.setCancelled(true);
            } else if (getInventoryData().categoryInventoryList.contains(event.getView().getTopInventory())) {
                if (getInventoryData().categoryInventoryList.contains(event.getClickedInventory())) {
                    if (event.getCurrentItem() != null) {
                        int clickedSlot = event.getSlot();

                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().openInventory(getCategoryRegistry().getCategories().get(clickedSlot).getPage(1));
                    }
                }
                event.setCancelled(true);
            } else if (getInventoryData().checkoutDataMap.containsKey(event.getView().getTopInventory())) {
                if (getInventoryData().checkoutDataMap.containsKey(event.getClickedInventory())) {
                    CategoryItem categoryItem = getInventoryData().checkoutDataMap.get(event.getClickedInventory());
                    if (event.getCurrentItem() != null) {
                        int clickedSlot = event.getSlot();
                        UserModel userModel = getUserRegistry().getUser(player.getUniqueId());
                        ServerType serverType = getConfigg().getServerType();

                        if ((clickedSlot >= 11 && clickedSlot <= 15)
                                || (clickedSlot >= 29 && clickedSlot <= 33)) {
                            int amount = 1;
                            if (clickedSlot >= 11 && clickedSlot <= 15) {
                                // BUY
                                amount = event.getClickedInventory().getItem(clickedSlot + 9).getAmount();

                                double price = amount * categoryItem.getBuyPrice();

                                if (userModel.getServerBalance(serverType) < price) {
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you do not have enough money in your account to purchase this."));
                                    event.setCancelled(true);
                                    return;
                                }

                                userModel.removeServerBalance(serverType, price);
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You bought " + GOLD + amount + AQUA + " "
                                        + categoryItem.enumName(categoryItem.getItemType().name()) + GRAY + " for " + GREEN + "$"
                                        + YELLOW + price + GRAY + "."));
                                player.getInventory().addItem(categoryItem.toItemStack(amount));
                                player.closeInventory();
                                player.openInventory(categoryItem.openCheckoutPage(player));
                            } else {
                                amount = event.getClickedInventory().getItem(clickedSlot - 9).getAmount();

                                double price = amount * categoryItem.getSellPrice();

                                if (!player.getInventory().contains(categoryItem.toItemStack().getType(), amount)) {
                                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you do not have " + GOLD + amount + " " + AQUA
                                            + categoryItem.enumName(categoryItem.getItemType().name()) + GRAY + " in your inventory."));
                                    event.setCancelled(true);
                                    return;
                                }

                                removeAmount(player, categoryItem.toItemStack().getType(), amount);
                                userModel.addServerBalance(serverType, price);
                                player.sendMessage(getMessages().getChatMessage(GRAY + "You sold " + GOLD + amount + AQUA + " "
                                        + categoryItem.enumName(categoryItem.getItemType().name()) + GRAY + " for " + GREEN + "$"
                                        + YELLOW + price + GRAY + "."));
                                player.closeInventory();
                                player.openInventory(categoryItem.openCheckoutPage(player));                            }

                        } else if (clickedSlot == 40) {
                            int amount = categoryItem.getAmountOfItems(player, new ItemStack(categoryItem.getItemType()));

                            double price = amount * categoryItem.getSellPrice();

                            if (!player.getInventory().contains(categoryItem.toItemStack().getType(), amount)) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you do not have " + GOLD + amount + " " + AQUA
                                        + categoryItem.enumName(categoryItem.getItemType().name()) + GRAY + " in your inventory."));
                                event.setCancelled(true);
                                return;
                            }

                            removeAmount(player, categoryItem.toItemStack().getType(), amount);
                            userModel.addServerBalance(serverType, price);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You sold " + GOLD + amount + AQUA + " "
                                    + categoryItem.enumName(categoryItem.getItemType().name()) + GRAY + " for " + GREEN + "$"
                                    + YELLOW + price + GRAY + "."));
                            player.closeInventory();
                            player.openInventory(categoryItem.openCheckoutPage(player));
                        } else if (clickedSlot == 45) {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().openInventory(getCategoryRegistry().getCategoryInventory());
                        } else if (clickedSlot == 53) {
                            event.getWhoClicked().closeInventory();
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (getInventoryData().categoryDataMap.containsKey(event.getInventory())) {
            getInventoryData().categoryDataMap.remove(event.getInventory());
        } else if (getInventoryData().categoryInventoryList.contains(event.getInventory())) {
            getInventoryData().categoryInventoryList.remove(event.getInventory());
        } else if (getInventoryData().checkoutDataMap.containsKey(event.getInventory())) {
            getInventoryData().checkoutDataMap.remove(event.getInventory());
        }
    }

    public static void removeAmount(Player p, Material m, int amount) {
        int remaining = amount;
        int slot = 0;
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getType() == m) {
                if (i.getAmount() > remaining) {
                    i.setAmount(i.getAmount() - remaining);
                    break;
                } else {
                    remaining = remaining - i.getAmount();
                    p.getInventory().setItem(slot, new ItemStack(Material.AIR));
                }
                if (remaining < 1) break;
            }
            slot++;
        }
        p.updateInventory();
    }
}
