package net.nighthawkempires.economy.commands;

import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.core.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.*;
import static org.bukkit.ChatColor.*;

public class BalanceCommand implements CommandExecutor {

    public BalanceCommand() {
        getCommandManager().registerCommands("balance", new String[]{
            "ne.balance"
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.balance")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            ServerType serverType = getConfigg().getServerType();

            switch (args.length) {
                case 0:
                    String amountString = String.format("%.2f", userModel.getServerBalance(serverType));

                    player.sendMessage(getMessages().getChatMessage(GRAY + "Your current balance is " + GREEN + "$"
                            + YELLOW + amountString + GRAY + "."));
                    return true;
                case 1:
                    String name = args[0];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                    if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                        player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                        return true;
                    }

                    userModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());
                    amountString = String.format("%.2f", userModel.getServerBalance(serverType));
                    player.sendMessage(getMessages().getChatMessage(GREEN + offlinePlayer.getName() + "'s " + GRAY + "current balance is " + GREEN + "$"
                            + YELLOW + amountString + GRAY + "."));
                    return true;
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}
