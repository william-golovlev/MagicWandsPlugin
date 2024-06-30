package org.golovlev.magicwands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MagicWandsListener implements Listener {
    private final Map<Player, List<Action>> actionSequences= new HashMap<>();
    private int arrowPenalty;
    private int potionPenalty;
    private int beastPenalty;
    private int lightningPenalty;
    private Boolean foodSpellIsEnabled;
    Plugin plugin = MagicWands.getInstance();

    MagicWandsListener(int arrowPenalty, int potionPenalty, int beastPenalty, int lightningPenalty, Boolean foodSpellIsEnabled) {
        this.arrowPenalty = arrowPenalty;
        this.potionPenalty = potionPenalty;
        this.beastPenalty = beastPenalty;
        this.lightningPenalty = lightningPenalty;
        this.foodSpellIsEnabled = foodSpellIsEnabled;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (!actionSequences.containsKey(player))
        {
            actionSequences.put(player, new ArrayList<Action>());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!actionSequences.containsKey(player)) {
            actionSequences.put(player, new ArrayList<Action>());
        }

        if (heldItem.getType() == Material.STICK && heldItem.hasItemMeta() &&
                heldItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "magical-wand")))
        {
            //EquipmentSlot.HAND test so it doesn't fire twice for offhand and hand.
            if (event.getHand() == EquipmentSlot.HAND) {
                if (action == Action.RIGHT_CLICK_AIR) {
                    player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Twirl");

                    if (actionSequences.containsKey(player)) {
                        addAction(Action.RIGHT_CLICK_AIR, player);
                    }

                    testActionsSequence(player);
                }
                else if (action == Action.RIGHT_CLICK_BLOCK) {
                    player.sendMessage( ChatColor.RED + ChatColor.BOLD.toString() + "Push");

                    if (actionSequences.containsKey(player)) {
                        addAction(Action.RIGHT_CLICK_BLOCK, player);
                    }

                    testActionsSequence(player);
                }
                else if (action == Action.LEFT_CLICK_BLOCK) {
                    player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Swing");
                    if (actionSequences.containsKey(player)) {
                        addAction(Action.LEFT_CLICK_BLOCK, player);
                    }

                    testActionsSequence(player);
                }
            }

        }
    }

    private void addAction(Action action, Player player) {
        List<Action> actions;
        actions = actionSequences.get(player);
        actions.add(action);
        actionSequences.put(player, actions);
    }

    private void testActionsSequence(Player player)
    {
        List<Action> actionList = actionSequences.get(player);
        int size = actionList.size();
        Spells cast = new Spells(plugin);


        //1 == half a bar of hunger
        int currentFoodLevel = player.getFoodLevel();
        int newFoodLevel;

        if (size >= 2)
        {
            Action mostRecentAction = actionList.get(size - 1);
            Action secondRecentAction = actionList.get(size - 2);

            //Summon an arrow!
            if (mostRecentAction == Action.RIGHT_CLICK_AIR &&
                    secondRecentAction == Action.RIGHT_CLICK_AIR)
            {
                player.sendMessage("You summon an arrow... and reset your moves!");
                cast.summonArrow(player);
                newFoodLevel = Math.max(currentFoodLevel - arrowPenalty, 0);
                player.setFoodLevel(newFoodLevel);
                player.setSaturation(0);
                actionSequences.put(player, new ArrayList<Action>()); //clear actions since we want to see new ones!
            }
            else if (mostRecentAction == Action.LEFT_CLICK_BLOCK &&
                    secondRecentAction == Action.RIGHT_CLICK_BLOCK)
            {
                player.sendMessage("You summon a potion... and reset your moves!");
                cast.summonPotion(player);
                newFoodLevel = Math.max(currentFoodLevel - potionPenalty, 0);
                player.setFoodLevel(newFoodLevel);
                player.setSaturation(0);
                actionSequences.put(player, new ArrayList<Action>()); //clear actions since we want to see new ones!

            }
            else if (size >= 3) {
                Action thirdRecentAction = actionList.get(size - 3);
                if (mostRecentAction == Action.RIGHT_CLICK_AIR && secondRecentAction == Action.LEFT_CLICK_BLOCK &&
                        thirdRecentAction == Action.LEFT_CLICK_BLOCK) {
                    player.sendMessage("You summon a bolt of lightning... and reset your moves!");
                    cast.summonLightning(player);
                    newFoodLevel = Math.max(currentFoodLevel - lightningPenalty, 0);
                    player.setFoodLevel(newFoodLevel);
                    player.setSaturation(0);
                    actionSequences.put(player, new ArrayList<Action>()); //clear actions if spell cast
                }
                else if (size >= 4) {
                    Action fourthRecentAction = actionList.get(size - 4);
                    if (mostRecentAction == Action.RIGHT_CLICK_BLOCK && secondRecentAction == Action.LEFT_CLICK_BLOCK &&
                            thirdRecentAction == Action.LEFT_CLICK_BLOCK && fourthRecentAction == Action.LEFT_CLICK_BLOCK) {
                        player.sendMessage("You summon a beast... and reset your moves!");
                        cast.summonBeast(player);
                        newFoodLevel = Math.max(currentFoodLevel - beastPenalty, 0);
                        player.setFoodLevel(newFoodLevel);
                        player.setSaturation(0);
                        actionSequences.put(player, new ArrayList<Action>()); //clear actions if spell cast
                    }
                }
            }


        }
    }
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
    {
        Player player = event.getPlayer();

        if (event.isSneaking() && foodSpellIsEnabled) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() == Material.STICK && heldItem.hasItemMeta() &&
                    heldItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "magical-wand")))
            {
                player.sendMessage(ChatColor.BOLD.toString() + "You have satiated your hunger...");
                player.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.EGG) {
            Egg egg = (Egg) entity;
            if (egg.hasMetadata("lightning")) {
                entity.getLocation().getWorld().strikeLightning(entity.getLocation());
            }
        }
    }
}