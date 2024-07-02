package org.golovlev.magicwands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {
    Plugin plugin = MagicWands.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("MagicWands.use")) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("magicwand")) {
                WandItem wand = new WandItem();
                boolean given = wand.giveWand(player);
                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("magicwandhelp")) {
                TutorialBook book = new TutorialBook();
                boolean given = book.giveTutorial(player);
                if (given) {
                    player.sendMessage("You have received the spell book...");
                }
                return true;
            }
        }
        else {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You don't have permission to use this command!");
            } else {
                sender.sendMessage("You are not a player...");
            }
        }
        return false;
    }
}
