package org.golovlev.magicwands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.lang.model.type.NullType;
import java.util.ArrayList;

public class TutorialBook {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    public TutorialBook() {
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("Spell Book");
        bookMeta.addEnchant(Enchantment.UNBREAKING, 10, true);
        bookMeta.setAuthor("Gandalf the Gray");

        ArrayList<String> pages = new ArrayList<>();
        pages.add("Welcome to the book of spells, young wizard...\nThere are mappings for 3 movements.\n\n" +
                ChatColor.AQUA + ChatColor.BOLD.toString() + "Twirl R-CLICK-AIR\n" +
                ChatColor.RED + ChatColor.BOLD.toString() + "Push R-CLICK-BLOCK\n" +
                ChatColor.YELLOW + ChatColor.BOLD.toString() + "Swing L-CLICK-BLOCK");
        pages.add("The first spell is an arrow spell. You can cast it the following way: \n\n" +
                ChatColor.AQUA + ChatColor.BOLD.toString() + "Twirl" + ChatColor.BLACK + " then " +
                ChatColor.AQUA + ChatColor.BOLD.toString() + " Twirl");
        pages.add("The second spell is a potion spell. You can cast it in the following way: \n\n" +
                ChatColor.RED + ChatColor.BOLD.toString() + "Push" + ChatColor.BLACK + " then " +
                ChatColor.YELLOW + ChatColor.BOLD.toString() + " Swing");
        pages.add("The third spell is a beast spell. You can cast it in the following way: \n\n" +
                ChatColor.YELLOW + ChatColor.BOLD.toString() + "Swing" + ChatColor.BLACK + " then " + ChatColor.YELLOW + ChatColor.BOLD.toString() + "Swing" + ChatColor.BLACK + " then " +
                ChatColor.YELLOW + ChatColor.BOLD.toString() + " Swing" +
                ChatColor.BLACK + " then " + ChatColor.RED + ChatColor.BOLD.toString() + " Push");
        pages.add("The fourth spell is a lightning spell. You can cast it in the following way: \n\n" +
                ChatColor.YELLOW + ChatColor.BOLD.toString() + "Swing" + ChatColor.BLACK + " then " + ChatColor.YELLOW + ChatColor.BOLD.toString() + " Swing" +
                ChatColor.BLACK + " then " + ChatColor.AQUA + ChatColor.BOLD.toString() + " Twirl");
        pages.add("Finally, you can cast hunger replenish if enabled:\n\n" + ChatColor.BOLD.toString() + "Sneak");
        bookMeta.setPages(pages);

        book.setItemMeta(bookMeta);
    }
    public boolean giveTutorial(Player player) {
        player.getInventory().addItem(book);
        return true;
    }
}
