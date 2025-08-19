package org.golovlev.magicwands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import java.util.*;

public class MagicWandsListener implements Listener {
    private final Map<Player, List<Action>> actionSequences = new HashMap<>();
    private final Map<UUID, List<Long>> playerCooldowns = new HashMap<>();

    Plugin plugin = MagicWands.getInstance();
    ConfigVars config = new ConfigVars();
    Boolean foodSpellIsEnabled = config.isFoodSpellEnabled();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!actionSequences.containsKey(player)) {
            actionSequences.put(player, new ArrayList<Action>());
        }
    }

    private boolean isMagicalWand(ItemStack item) {
        return item != null && item.getType() == Material.STICK && item.hasItemMeta() &&
                item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "magical-wand"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!actionSequences.containsKey(player)) {
            actionSequences.put(player, new ArrayList<Action>());
        }

        if (isMagicalWand(heldItem)) {
            if (event.getHand() == EquipmentSlot.HAND) {
                if (action == Action.RIGHT_CLICK_AIR) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Twirl");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.BLOCK_WOOL_BREAK, 0.5F, 1.5F);

                    if (actionSequences.containsKey(player)) {
                        addAction(Action.RIGHT_CLICK_AIR, player);
                    }
                    testActionsSequence(player);
                } else if (action == Action.LEFT_CLICK_AIR) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.GREEN + ChatColor.BOLD.toString() + "Thrust");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F, 1.2F);

                    if (actionSequences.containsKey(player)) {
                        addAction(Action.LEFT_CLICK_AIR, player);
                    }
                    testActionsSequence(player);
                } else if (action == Action.RIGHT_CLICK_BLOCK) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.RED + ChatColor.BOLD.toString() + "Push");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.5F, 1.0F);

                    if (actionSequences.containsKey(player)) {
                        addAction(Action.RIGHT_CLICK_BLOCK, player);
                    }
                    testActionsSequence(player);
                } else if (action == Action.LEFT_CLICK_BLOCK) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Swing");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.5F, 0.8F);

                    if (actionSequences.containsKey(player)) {
                        addAction(Action.LEFT_CLICK_BLOCK, player);
                    }
                    testActionsSequence(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        if (isMagicalWand(previousItem) && !isMagicalWand(newItem)) {
            // Player has switched away from the magical wand.
            actionSequences.put(player, new ArrayList<Action>());
            BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.GRAY + "Spell sequence reset.");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.5F, 0.5F); // A subtle sound
        }
    }

    private void addAction(Action action, Player player) {
        List<Action> actions = actionSequences.get(player);
        actions.add(action);
        actionSequences.put(player, actions);
    }

    private void testActionsSequence(Player player) {
        int arrowPenalty = config.getArrowFoodPenalty();
        int arrowCooldown = config.getArrowCooldown();
        int potionPenalty = config.getPotionFoodPenalty();
        int potionCooldown = config.getPotionCooldown();
        int lightningPenalty = config.getLightningFoodPenalty();
        int lightningCooldown = config.getLightningCooldown();
        int beastPenalty = config.getBeastFoodPenalty();
        int beastCooldown = config.getBeastCooldown();
        Boolean beastEnabled = config.isBeastSpellEnabled();
        int flamePenalty = config.getFlameFoodPenalty();
        int flameCooldown = config.getFlameCooldown();
        int explosionPenalty = config.getExplosionFoodPenalty();
        int explosionCooldown = config.getExplosionCooldown();

        long currentTime = System.currentTimeMillis() / 1000;

        if (!playerCooldowns.containsKey(player.getUniqueId())) {
            ArrayList<Long> cooldowns = new ArrayList<>();
            cooldowns.add((long) arrowCooldown);
            cooldowns.add((long) potionCooldown);
            cooldowns.add((long) lightningCooldown);
            cooldowns.add((long) beastCooldown);
            cooldowns.add((long) flameCooldown);
            cooldowns.add((long) explosionCooldown);
            playerCooldowns.put(player.getUniqueId(), cooldowns);
        }

        List<Action> actionList = actionSequences.get(player);
        int size = actionList.size();
        Spells cast = new Spells(plugin);

        int currentFoodLevel = player.getFoodLevel();
        int newFoodLevel;

        if (size >= 2) {
            Action mostRecentAction = actionList.get(size - 1);
            Action secondRecentAction = actionList.get(size - 2);

            if (mostRecentAction == Action.RIGHT_CLICK_AIR && secondRecentAction == Action.RIGHT_CLICK_AIR) {
                List<Long> allCooldowns = playerCooldowns.get(player.getUniqueId());
                long timeElapsed = currentTime - allCooldowns.get(0);
                if (timeElapsed >= arrowCooldown) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.AQUA + "You summon an arrow... and reset your moves!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

                    cast.summonArrow(player);
                    newFoodLevel = Math.max(currentFoodLevel - arrowPenalty, 0);
                    player.setFoodLevel(newFoodLevel);
                    player.setSaturation(0);
                    allCooldowns.set(0, currentTime);
                    playerCooldowns.put(player.getUniqueId(), allCooldowns);
                    actionSequences.put(player, new ArrayList<Action>());
                } else {
                    BaseComponent[] components = TextComponent.fromLegacyText("Arrow spell is on cooldown for " + (arrowCooldown - timeElapsed) + "s");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_HIT, 0.5F, 1.5F);
                }
            } else if (mostRecentAction == Action.LEFT_CLICK_BLOCK && secondRecentAction == Action.RIGHT_CLICK_BLOCK) {
                List<Long> allCooldowns = playerCooldowns.get(player.getUniqueId());
                long timeElapsed = currentTime - allCooldowns.get(1);
                if (timeElapsed >= potionCooldown) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "You summon a potion... and reset your moves!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 1.0F, 1.0F);

                    cast.summonPotion(player);
                    newFoodLevel = Math.max(currentFoodLevel - potionPenalty, 0);
                    player.setFoodLevel(newFoodLevel);
                    player.setSaturation(0);
                    allCooldowns.set(1, currentTime);
                    playerCooldowns.put(player.getUniqueId(), allCooldowns);
                    actionSequences.put(player, new ArrayList<Action>());
                } else {
                    BaseComponent[] components = TextComponent.fromLegacyText("Potion spell is on cooldown for " + (potionCooldown - timeElapsed) + "s");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5F, 1.5F);
                }
            } else if (mostRecentAction == Action.LEFT_CLICK_AIR && secondRecentAction == Action.LEFT_CLICK_AIR) {
                List<Long> allCooldowns = playerCooldowns.get(player.getUniqueId());
                long timeElapsed = currentTime - allCooldowns.get(4);
                if (timeElapsed >= flameCooldown) {
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.GOLD + "You summon a burst of fire... and reset your moves!");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F);

                    cast.summonFlameShove(player);
                    newFoodLevel = Math.max(currentFoodLevel - flamePenalty, 0);
                    player.setFoodLevel(newFoodLevel);
                    player.setSaturation(0);
                    allCooldowns.set(4, currentTime);
                    playerCooldowns.put(player.getUniqueId(), allCooldowns);
                    actionSequences.put(player, new ArrayList<Action>());
                } else {
                    BaseComponent[] components = TextComponent.fromLegacyText("Fire burst spell is on cooldown for " + (flameCooldown - timeElapsed) + "s");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                    player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5F, 2.0F);
                }
            } else if (size >= 3) {
                Action thirdRecentAction = actionList.get(size - 3);
                if (mostRecentAction == Action.RIGHT_CLICK_AIR && secondRecentAction == Action.LEFT_CLICK_BLOCK &&
                        thirdRecentAction == Action.LEFT_CLICK_BLOCK) {
                    List<Long> allCooldowns = playerCooldowns.get(player.getUniqueId());
                    long timeElapsed = currentTime - allCooldowns.get(2);

                    if (timeElapsed >= lightningCooldown) {
                        BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.YELLOW + "You summon a bolt of lightning... and reset your moves!");
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);

                        cast.summonLightning(player);
                        newFoodLevel = Math.max(currentFoodLevel - lightningPenalty, 0);
                        player.setFoodLevel(newFoodLevel);
                        player.setSaturation(0);
                        allCooldowns.set(2, currentTime);
                        playerCooldowns.put(player.getUniqueId(), allCooldowns);
                        actionSequences.put(player, new ArrayList<Action>());
                    } else {
                        BaseComponent[] components = TextComponent.fromLegacyText("Lightning spell is on cooldown for " + (lightningCooldown - timeElapsed) + "s");
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.5F, 0.5F);
                    }
                } else if (mostRecentAction == Action.RIGHT_CLICK_BLOCK && secondRecentAction == Action.LEFT_CLICK_AIR && thirdRecentAction == Action.RIGHT_CLICK_BLOCK) {
                    List<Long> allCooldowns = playerCooldowns.get(player.getUniqueId());
                    long timeElapsed = currentTime - allCooldowns.get(5);

                    if (timeElapsed >= explosionCooldown) {
                        BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.DARK_RED + "You summon a fiery explosion... and reset your moves!");
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);

                        cast.summonExplosion(player);
                        newFoodLevel = Math.max(currentFoodLevel - explosionPenalty, 0);
                        player.setFoodLevel(newFoodLevel);
                        player.setSaturation(0);
                        allCooldowns.set(5, currentTime);
                        playerCooldowns.put(player.getUniqueId(), allCooldowns);
                        actionSequences.put(player, new ArrayList<Action>());
                    } else {
                        BaseComponent[] components = TextComponent.fromLegacyText("Fiery explosion spell is on cooldown for " + (explosionCooldown - timeElapsed) + "s");
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                        player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5F, 1.5F);
                    }
                } else if (size >= 4) {
                    Action fourthRecentAction = actionList.get(size - 4);
                    if (mostRecentAction == Action.RIGHT_CLICK_BLOCK && secondRecentAction == Action.LEFT_CLICK_BLOCK &&
                            thirdRecentAction == Action.LEFT_CLICK_BLOCK && fourthRecentAction == Action.LEFT_CLICK_BLOCK && beastEnabled) {
                        List<Long> allCooldowns = playerCooldowns.get(player.getUniqueId());
                        long timeElapsed = currentTime - allCooldowns.get(3);

                        if (timeElapsed >= beastCooldown) {
                            BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.DARK_AQUA + "You summon a beast... and reset your moves!");
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                            player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 1.0F, 0.8F);

                            cast.summonBeast(player);
                            newFoodLevel = Math.max(currentFoodLevel - beastPenalty, 0);
                            player.setFoodLevel(newFoodLevel);
                            player.setSaturation(0);
                            allCooldowns.set(3, currentTime);
                            playerCooldowns.put(player.getUniqueId(), allCooldowns);
                            actionSequences.put(player, new ArrayList<Action>());
                        } else {
                            BaseComponent[] components = TextComponent.fromLegacyText("Beast spell is on cooldown for " + (beastCooldown - timeElapsed) + "s");
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
                            player.playSound(player.getLocation(), Sound.ENTITY_FOX_SCREECH, 0.5F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && foodSpellIsEnabled) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (isMagicalWand(heldItem)) {
                player.sendMessage(ChatColor.BOLD.toString() + "You have satiated your hunger...");
                player.setFoodLevel(20);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0F, 1.0F);
            }
        }
    }

    Entity flamePearl = null;
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.EGG) {
            Egg egg = (Egg) entity;
            if (egg.hasMetadata("lightning")) {
                entity.getLocation().getWorld().strikeLightning(entity.getLocation());
            }
        }
        if (entity.getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) entity;
            if (arrow.getShooter() instanceof Player && arrow.hasMetadata("arrow-spell")) {
                Entity hitEntity = event.getHitEntity();
                arrow.setDamage(config.getArrowDamage());
                if (hitEntity != null) {
                    Location hitLocation = hitEntity.getLocation();
                    spawnFireworkExplosion(hitLocation);
                    arrow.remove();
                }
            }
        }
        if (entity.getType() == EntityType.ENDER_PEARL) {
            EnderPearl ender = (EnderPearl) entity;
            if (ender.getShooter() instanceof Player && ender.hasMetadata("flame-spell")) {
                flamePearl = ender;
                event.setCancelled(true);
                Entity hitEntity = event.getHitEntity();
                if (hitEntity != null) {
                    hitEntity.setFireTicks(100);
                    double distance = config.getFlameKnockback();
                    Vector knockback = ((Player) ender.getShooter()).getLocation().getDirection().multiply(distance * 1.5).setY(distance);
                    hitEntity.setVelocity(knockback);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && flamePearl != null) {
            event.setCancelled(true);
        }
    }

    private void spawnFireworkExplosion(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(Color.AQUA)
                .with(FireworkEffect.Type.BALL)
                .withFlicker()
                .withTrail()
                .build());
        firework.setFireworkMeta(fireworkMeta);
        firework.setVelocity(new Vector(0, 0, 0));
        firework.detonate();
    }

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent event) {
        //todo
    }

    @EventHandler
    public void onEggHit(PlayerEggThrowEvent event) {
        Egg egg = event.getEgg();
        if (egg.hasMetadata("lightning")) {
            event.setHatching(false);
        }
    }
}