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
        config.addDefault("summonPotion_Food_Penalty", 2);
        config.addDefault("summonArrow_Food_Penalty", 1);
        config.addDefault("summonBeast_Food_Penalty", 8);
        config.addDefault("summonLightning_Food_Penalty", 6);
        config.addDefault("enableFoodSpell", true);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public int getPotionFoodPenalty() {
        return config.getInt("summonPotion_Food_Penalty");
    }

    public int getArrowFoodPenalty() {
        return config.getInt("summonArrow_Food_Penalty");
    }

    public int getBeastFoodPenalty() {
        return config.getInt("summonBeast_Food_Penalty");
    }

    public int getLightningFoodPenalty() {
        return config.getInt("summonLightning_Food_Penalty");
    }

    public Boolean isFoodSpellEnabled() {
        return config.getBoolean("enableFoodSpell");
    }
}
