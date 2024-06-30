package org.golovlev.magicwands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class WandItem {
    public NamespacedKey key;
    public boolean giveWand(Player player) {
        if (player == null) {
            return false;
        }

        ItemStack wand = new ItemStack(Material.STICK);

        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Magic Wand");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Highly fatiguing to use..."));
        NamespacedKey key = new NamespacedKey(MagicWands.getInstance(), "magical-wand");
        this.key = key;
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "Magic Wand");

        wand.setItemMeta(meta);
        player.getInventory().addItem(wand);
        player.sendMessage("You got the magic wand!");
        return true;
    }
}
