package net.nighthawkempires.economy.commands;

import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.core.user.UserModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static org.bukkit.ChatColor.*;

public class EconomyCommand implements CommandExecutor {
    public EconomyCommand() {
        getCommandManager().registerCommands("economy", new String[] {
                "ne.economy"
        });
    }

    private String[] help = new String[] {
            getMessages().getMessage(CHAT_HEADER),
            DARK_GRAY + "Command" + GRAY + ": Economy    " + DARK_GRAY + "    [Optional], <Required>",
            getMessages().getMessage(CHAT_FOOTER),
            getMessages().getCommand("economy", "set [player] <balance>", "Set a player's balance"),
            getMessages().getCommand("economy", "add [player] <amount>", "Add to a player's balance"),
            getMessages().getCommand("economy", "remove [player] <amount>", "Remove from a player's balance"),
            getMessages().getMessage(CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.give")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            ServerType serverType = getConfigg().getServerType();

            switch (args.length) {
                case 0:
                    player.sendMessage(help);
                    return true;
                case 2:
                    UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

                    if (!NumberUtils.isParsable(args[1])) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                        return true;
                    }

                    double amount = Double.parseDouble(args[1]);

                    String amountString = String.format("%.2f", amount);

                    switch (args[0].toLowerCase()) {
                        case "set":
                            userModel.setServerBalance(serverType, amount);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have set your balance to "
                                    + GREEN + "$" + YELLOW + amountString + GRAY + "."));
                            return true;
                        case "add":
                            userModel.addServerBalance(serverType, amount);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have added " + GREEN + "$" + YELLOW + amountString +
                                    GRAY + " to your balance."));
                            return true;
                        case "remove":
                            userModel.removeServerBalance(serverType, amount);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have removed " + GREEN + "$" + YELLOW + amountString +
                                    GRAY + " from your balance."));
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                            return true;
                    }
                case 3:
                    String name = args[1];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

                    if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                        player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                        return true;
                    }

                    userModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());

                    if (!NumberUtils.isParsable(args[2])) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                        return true;
                    }

                    amount = Double.parseDouble(args[2]);

                    amountString = String.format("%.2f", amount);

                    switch (args[0].toLowerCase()) {
                        case "set":
                            userModel.setServerBalance(serverType, amount);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have set " + GREEN + offlinePlayer.getName() + "'s " +
                                    GRAY + "balance to " + GREEN + "$" + YELLOW + amountString + GRAY + "."));
                            return true;
                        case "add":
                            userModel.addServerBalance(serverType, amount);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have added " + GREEN + "$" + YELLOW + amountString +
                                    GRAY + " to " + GREEN + offlinePlayer.getName() + "'s " + GRAY + "balance."));
                            return true;
                        case "remove":
                            userModel.removeServerBalance(serverType, amount);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have removed " + GREEN + "$" + YELLOW + amountString +
                                    GRAY + " from " + GREEN + offlinePlayer.getName() + "'s " + GRAY + "balance."));
                            return true;
                        default:
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
