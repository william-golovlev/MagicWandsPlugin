package org.golovlev.magicwands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigVars {
    private final MagicWands plugin = MagicWands.getInstance();
    public FileConfiguration config;

    public ConfigVars() {
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
//        config.addDefault("summonPotion_Food_Penalty", 2);
//        config.addDefault("summonPotion_Cooldown", 30);
//        config.addDefault("summonArrow_Food_Penalty", 1);
//        config.addDefault("summonArrow_Cooldown", 0);
//        config.addDefault("summonBeast_Food_Penalty", 8);
//        config.addDefault("summonBeast_Cooldown", 300);
//        config.addDefault("summonLightning_Food_Penalty", 6);
//        config.addDefault("summonLightning_Cooldown", 60);
//        config.addDefault("enableFoodSpell", true);


        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public int getPotionFoodPenalty() {
        return config.getInt("summonPotion_Food_Penalty");
    }

    public int getPotionCooldown() {
        return config.getInt("summonPotion_Cooldown");

    }

    public int getArrowFoodPenalty() {
        return config.getInt("summonArrow_Food_Penalty");
    }

    public int getArrowCooldown() {
        return config.getInt("summonArrow_Cooldown");
    }

    public double getArrowDamage() {
        return config.getDouble("summonArrow_damage");
    }

    public int getBeastFoodPenalty() {
        return config.getInt("summonBeast_Food_Penalty");
    }

    public int getBeastCooldown() {
        return config.getInt("summonBeast_Cooldown");
    }

    public int getLightningFoodPenalty() {
        return config.getInt("summonLightning_Food_Penalty");
    }

    public int getLightningCooldown() {
        return config.getInt("summonLightning_Cooldown");
    }

    public int getFlameFoodPenalty() {
        return config.getInt("summonFlame_Food_Penalty");
    }

    public int getFlameCooldown() {
        return config.getInt("summonFlame_Cooldown");
    }

    public int getExplosionFoodPenalty() {
        return config.getInt("summonExplosion_Food_Penalty");
    }

    public int getExplosionCooldown() {
        return config.getInt("summonExplosion_Cooldown");
    }

    public double getFlameKnockback() {
        return config.getDouble("summonFlame_Knockback_Level");
    }

    public Boolean isFoodSpellEnabled() {
        return config.getBoolean("enableFoodSpell");
    }

    public Boolean isBeastSpellEnabled() {
        return config.getBoolean("enableBeastSpell");
    }
}
