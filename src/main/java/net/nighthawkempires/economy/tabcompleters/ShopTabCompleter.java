package net.nighthawkempires.economy.tabcompleters;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

import static net.nighthawkempires.economy.EconomyPlugin.getCategoryRegistry;

public class ShopTabCompleter implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.shop")) {
                return completions;
            }

            switch (args.length) {
                case 0:
                case 1:
                    StringUtil.copyPartialMatches(args[0], getCategoryRegistry().getRegisteredData().keySet(), completions);
                    Collections.sort(completions);
                    return completions;
                default:
            }
        }
        return completions;
    }
}
