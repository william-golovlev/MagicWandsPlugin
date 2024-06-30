package org.golovlev.magicwands;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Spells {
    Plugin plugin;
    public Spells(Plugin plugin) {
        this.plugin = plugin;
    }
    public void summonPotion(Player player)
    {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);

        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1), true);

        potion.setItemMeta(potionMeta);

        player.getWorld().spawn(player.getLocation(), ThrownPotion.class, thrownPotion -> {
            thrownPotion.setItem(potion);
            thrownPotion.setShooter(player);
        });
    }

    public void summonArrow(Player player) {
        Vector eyeDirection = player.getEyeLocation().getDirection();
        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), eyeDirection, 1.0f, 0.0f);

        arrow.setShooter(player);
        arrow.setVelocity(eyeDirection.multiply(3.0));
    }

    public void summonBeast(Player player) {
        World world = player.getWorld();
        Location spawnLocation = player.getLocation();

        Wolf beast = (Wolf) world.spawnEntity(spawnLocation, EntityType.WOLF);

        beast.setCustomName(ChatColor.BOLD.toString() + "Beast");
        beast.setTamed(true);
        beast.setCollarColor(DyeColor.RED);
        beast.setAngry(true);
        beast.setOwner(player);

        beast.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
    }

    public void summonLightning(Player player) {
        Egg egg = player.launchProjectile(Egg.class);
        egg.setMetadata("lightning", new FixedMetadataValue(plugin, "egg"));
    }
}
