package net.nighthawkempires.economy.commands;

import com.google.common.collect.Lists;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.core.user.UserModel;
import net.nighthawkempires.core.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.core.lang.Messages.CHAT_FOOTER;
import static net.nighthawkempires.core.lang.Messages.CHAT_HEADER;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.DARK_GRAY;

public class BalanceTopCommand implements CommandExecutor {

    public BalanceTopCommand() {
        getCommandManager().registerCommands("balancetop", new String[]{
                "ne.balance"
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.balance")) {
                player.sendMessage(CorePlugin.getMessages().getChatTag(Messages.NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    sendBalanceTop(sender, 1);
                    return true;
                case 1:
                    if (!NumberUtils.isDigits(args[0])) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "The page must be a valid number."));
                        return true;
                    }

                    int page = Integer.parseInt(args[0]);

                    if (page < 1 || page > getTotalBalanceTopPages()) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "That is not a valid pages.  The range is 1 - " + getTotalBalanceTopPages() + "."));
                        return true;
                    }

                    sendBalanceTop(player, page);
                    return true;
                default:
                    player.sendMessage(CorePlugin.getMessages().getChatTag(Messages.INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }

    public static int getTotalBalanceTopPages() {
        return (int) Math.ceil((double) getUserRegistry().getUsers().size() / 10);
    }

    private void sendBalanceTop(CommandSender sender, int page) {
        int displayPage = page;
        page = page - 1;

        List<UserModel> users = Lists.newArrayList(getUserRegistry().getUsers());
        users.sort(Comparator.comparing(UserModel::getCurrentServerBalance).reversed());

        int start = 10 * page;
        int finish = Math.min(start + 10, users.size());

        String[] help = new String[]{
                getMessages().getMessage(CHAT_HEADER),
                getBalanceTopHeader(displayPage, getTotalBalanceTopPages()),
                getMessages().getMessage(CHAT_FOOTER)
        };
        sender.sendMessage(help);

        for (int i = start; i < finish; i++) {
            UserModel user = users.get(i);
            UUID uuid = UUID.fromString(user.getKey());
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            StringBuilder spacer = new StringBuilder();
            int spaces = 25 - offlinePlayer.getName().length();
            for (int j = 0; j < spaces; j++)
                spacer.append(" ");

            int pos = i + 1;
            sender.sendMessage(GOLD + "" + pos + DARK_GRAY + ". " + GREEN + offlinePlayer.getName()
                    + spacer.toString() + DARK_GRAY + " - Balance" + GRAY + ": " + GOLD + StringUtil.formatBalance(user.getCurrentServerBalance()));
        }

        sender.sendMessage(getMessages().getMessage(CHAT_FOOTER));
    }

    public String getBalanceTopHeader(int page, int total) {
        return DARK_GRAY + "Balance Top    " + DARK_GRAY + "-    Page" + GRAY + ": " + DARK_GRAY + "[" + GOLD + ""
                + UNDERLINE + page + DARK_GRAY + "/" + GOLD + "" + UNDERLINE + total + DARK_GRAY + "]";
    }
}
