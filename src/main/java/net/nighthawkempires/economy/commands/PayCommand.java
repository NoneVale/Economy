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
import static net.nighthawkempires.core.CorePlugin.getUserRegistry;
import static net.nighthawkempires.core.lang.Messages.*;
import static org.bukkit.ChatColor.*;

public class PayCommand implements CommandExecutor {

    public PayCommand() {
        getCommandManager().registerCommands("pay", new String[] {
                "ne.pay"
        });
    }

    private String[] help = new String[] {
            getMessages().getMessage(CHAT_HEADER),
            DARK_GRAY + "Command" + GRAY + ": Pay    " + DARK_GRAY + "    [Optional], <Required>",
            getMessages().getMessage(CHAT_FOOTER),
            getMessages().getCommand("pay", "<player> <balance>", "Send money to a player"),
            getMessages().getMessage(CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.pay")) {
                player.sendMessage(getMessages().getChatTag(NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    player.sendMessage(help);
                    return true;
                case 2:
                    String name = args[0];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

                    if (!getUserRegistry().userExists(offlinePlayer.getUniqueId())) {
                        player.sendMessage(getMessages().getChatTag(PLAYER_NOT_FOUND));
                        return true;
                    }

                    UserModel userModel = getUserRegistry().getUser(player.getUniqueId());
                    UserModel targetUserModel = getUserRegistry().getUser(offlinePlayer.getUniqueId());

                    ServerType serverType = getConfigg().getServerType();

                    if (!NumberUtils.isParsable(args[1])) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "The amount must be a valid number."));
                        return true;
                    }

                    double amount = Double.parseDouble(args[1]);

                    if (userModel.getServerBalance(serverType) < amount) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you do not have enough money in order to do that."));
                        return true;
                    }

                    userModel.removeServerBalance(serverType, amount);
                    targetUserModel.addServerBalance(serverType, amount);
                    player.sendMessage(getMessages().getChatMessage(GRAY + "You paid " + offlinePlayer.getName() + " $" + YELLOW + amount + GRAY + "."));
                    if (offlinePlayer.isOnline())
                        offlinePlayer.getPlayer().sendMessage(getMessages().getChatMessage(GREEN + player.getName() + GRAY + " has paid you "
                                + GREEN + "$" + YELLOW + amount + GRAY + "."));
                    return true;
                default:
                    player.sendMessage(getMessages().getChatTag(INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}
