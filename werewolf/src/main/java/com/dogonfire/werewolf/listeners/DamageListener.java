package com.dogonfire.werewolf.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import com.dogonfire.werewolf.ClanManager;
import java.util.Random;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftWolf;
import org.bukkit.util.Vector;
import org.bukkit.Effect;
import com.dogonfire.werewolf.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.EntityEffect;
import org.bukkit.event.EventHandler;
import com.dogonfire.werewolf.WerewolfSkin;
import net.minecraft.server.v1_7_R1.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import com.dogonfire.werewolf.Werewolf;
import org.bukkit.event.Listener;

public class DamageListener implements Listener
{
    public static double HAND_DAMAGE;
    public static double ITEM_DAMAGE;
    public static double SILVER_MULTIPLIER;
    public static double armorMultiplier;
    public static String WEREWOLF_GROWL;
    private Werewolf plugin;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType;
    
    static {
        DamageListener.HAND_DAMAGE = 8.0;
        DamageListener.ITEM_DAMAGE = 3.0;
        DamageListener.SILVER_MULTIPLIER = 2.0;
        DamageListener.armorMultiplier = 0.25;
        DamageListener.WEREWOLF_GROWL = "";
    }
    
    public DamageListener(final Werewolf plugin) {
        super();
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamage1(final EntityDamageEvent event) {
        if (!Werewolf.pluginEnabled) {
            return;
        }
        if (!event.isCancelled() && event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getName())) {
                final WerewolfSkin skin = Werewolf.getSkinManager().getSkin(player);
                if (skin != null) {
                    Werewolf.getSkinManager().sendPacketsToWorld(player.getWorld(), new Packet[] { skin.getAnimationPacket(1) });
                    Werewolf.getWerewolfManager().setPouncing(player.getName());
                }
                else {
                    this.plugin.logDebug("onEntityDamage: Skin is null for " + player.getName());
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage2(final EntityDamageEvent event) {
        if (!Werewolf.pluginEnabled) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            final Player werewolfPlayer = (Player)event.getEntity();
            if (Werewolf.getWerewolfManager().hasWerewolfSkin(werewolfPlayer.getName())) {
                double damage = event.getDamage();
                switch ($SWITCH_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause()[event.getCause().ordinal()]) {
                    case 2:
                    case 11:
                    case 12:
                    case 19: {
                        damage *= DamageListener.armorMultiplier;
                        break;
                    }
                    case 5: {
                        damage = 0.0;
                        break;
                    }
                }
                if (Werewolf.getClanManager().isAlpha(werewolfPlayer.getName())) {
                    damage /= 2.0;
                }
                event.setDamage(damage);
                if (damage == 0.0) {
                    event.setCancelled(true);
                    return;
                }
                werewolfPlayer.playEffect(EntityEffect.HURT);
                Werewolf.getWerewolfManager().growl(werewolfPlayer);
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent)event;
            if (damageEvent.getDamager() instanceof Player) {
                final Player player = (Player)damageEvent.getDamager();
                this.plugin.logDebug(String.valueOf(player.getName()) + " is doing damage to " + damageEvent.getEntityType().name());
                if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getName())) {
                    double damage2 = 0.0;
                    if (player.getItemInHand().getType().equals((Object)Material.AIR)) {
                        damage2 = DamageListener.HAND_DAMAGE;
                    }
                    else {
                        damage2 = DamageListener.ITEM_DAMAGE;
                    }
                    if (Werewolf.getClanManager().isAlpha(player.getName())) {
                        damage2 *= 2.0;
                    }
                    event.setDamage(damage2);
                    if (damageEvent.getEntity() instanceof Player) {
                        final Player victim = (Player)damageEvent.getEntity();
                        if (!this.plugin.isVampire(victim) && Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infectother") && Werewolf.getPermissionsManager().hasPermission(victim, "werewolf.becomeinfected")) {
                            if (!Werewolf.getWerewolfManager().isWerewolf(victim)) {
                                if (Math.random() < this.plugin.werewolfInfectionRisk) {
                                    final ClanManager.ClanType clanType = Werewolf.getWerewolfManager().getWerewolfClan(player.getName());
                                    Werewolf.getWerewolfManager().makeWerewolf(victim, false, clanType);
                                    Werewolf.getLanguageManager().setPlayerName(player.getName());
                                    victim.sendMessage(ChatColor.LIGHT_PURPLE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BiteVictim));
                                    Werewolf.getLanguageManager().setPlayerName(victim.getName());
                                    player.sendMessage(ChatColor.LIGHT_PURPLE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BiteAttacker));
                                    victim.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 100);
                                    victim.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
                                    victim.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
                                    this.plugin.logDebug(String.valueOf(player.getName()) + " infected " + victim.getName() + "!");
                                }
                            }
                            else {
                                final ClanManager.ClanType killerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getName());
                                final ClanManager.ClanType victimClan = Werewolf.getWerewolfManager().getWerewolfClan(victim.getName());
                                if (killerClan == victimClan) {
                                    if (Werewolf.getClanManager().isAlpha(killerClan, victim.getName())) {
                                        event.setCancelled(false);
                                        return;
                                    }
                                    this.plugin.logDebug(String.valueOf(player.getName()) + " and " + victim.getName() + " is within the same clan! Damage cancelled.");
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            if (event.getEntity() != null && event.getEntity() instanceof Player) {
                final Player player = (Player)event.getEntity();
                if (!Werewolf.getWerewolfManager().hasWerewolfSkin(player.getName()) && (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.becomeinfected") || player.isOp()) && !Werewolf.getWerewolfManager().isWerewolf(player) && damageEvent.getDamager() instanceof CraftWolf && Math.random() < this.plugin.wildWolfInfectionRisk) {
                    final Random random = new Random();
                    Werewolf.getWerewolfManager().makeWerewolf(player, false, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BiteVictim));
                }
            }
        }
        if (event.getDamage() <= 0.0) {
            event.setDamage(1.0);
        }
    }
    
    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        if (!this.plugin.vaultEnabled) {
            return;
        }
        if (event.getEntity().getKiller() == null) {
            return;
        }
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }
        final Player killer = event.getEntity().getKiller();
        if (Werewolf.getWerewolfManager().hasWerewolfSkin(killer.getName())) {
            int health = 0;
            switch ($SWITCH_TABLE$org$bukkit$entity$EntityType()[event.getEntity().getType().ordinal()]) {
                case 33: {
                    health = 2;
                    break;
                }
                case 48: {
                    health = 4;
                    break;
                }
                case 44: {
                    health = 4;
                    break;
                }
                case 46: {
                    health = 1;
                    break;
                }
                case 43: {
                    health = 1;
                    break;
                }
                case 27: {
                    health = 3;
                    break;
                }
                case 45: {
                    health = 2;
                    break;
                }
                case 42: {
                    health = 3;
                    break;
                }
                case 26: {
                    health = 2;
                    break;
                }
                case 50: {
                    health = 1;
                    break;
                }
                case 29: {
                    health = 2;
                    break;
                }
                case 40: {
                    health = 1;
                    break;
                }
            }
            if (health > 0 && killer.getMaxHealth() < killer.getMaxHealth()) {
                Werewolf.getLanguageManager().setAmount(new StringBuilder().append(health).toString());
                killer.sendMessage(ChatColor.LIGHT_PURPLE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.KilledMob));
                if (killer.getHealth() + health > killer.getMaxHealth()) {
                    killer.setHealth(killer.getMaxHealth());
                }
                else {
                    killer.setHealth(killer.getHealth() + health);
                }
            }
            if (this.plugin.useClans) {
                final ClanManager.ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(killer.getName());
                Werewolf.getClanManager().handleMobKill(killer, clan, event.getEntity().getType());
            }
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player werewolf = (Player)event.getEntity();
        if (!Werewolf.getWerewolfManager().hasWerewolfSkin(werewolf.getName())) {
            return;
        }
        if (this.plugin.vaultEnabled && !Werewolf.getWerewolfManager().hasWerewolfSkin(killer.getName())) {
            Werewolf.getHuntManager().handleKill(killer.getName());
        }
    }
    
    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause() {
        final int[] $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause = DamageListener.$SWITCH_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause;
        if ($switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause != null) {
            return $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause;
        }
        final int[] $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2 = new int[EntityDamageEvent.DamageCause.values().length];
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.BLOCK_EXPLOSION.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.CONTACT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.CUSTOM.ordinal()] = 22;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.DROWNING.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.ENTITY_ATTACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.ENTITY_EXPLOSION.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError6) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.FALL.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError7) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.FALLING_BLOCK.ordinal()] = 20;
        }
        catch (NoSuchFieldError noSuchFieldError8) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.FIRE.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError9) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.FIRE_TICK.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError10) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.LAVA.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError11) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.LIGHTNING.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError12) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.MAGIC.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError13) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.MELTING.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError14) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.POISON.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError15) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.PROJECTILE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError16) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.STARVATION.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError17) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.SUFFOCATION.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError18) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.SUICIDE.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError19) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.THORNS.ordinal()] = 21;
        }
        catch (NoSuchFieldError noSuchFieldError20) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.VOID.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError21) {}
        try {
            $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2[EntityDamageEvent.DamageCause.WITHER.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError22) {}
        return DamageListener.$SWITCH_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause = $switch_TABLE$org$bukkit$event$entity$EntityDamageEvent$DamageCause2;
    }
    
    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType() {
        final int[] $switch_TABLE$org$bukkit$entity$EntityType = DamageListener.$SWITCH_TABLE$org$bukkit$entity$EntityType;
        if ($switch_TABLE$org$bukkit$entity$EntityType != null) {
            return $switch_TABLE$org$bukkit$entity$EntityType;
        }
        final int[] $switch_TABLE$org$bukkit$entity$EntityType2 = new int[EntityType.values().length];
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ARROW.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.BAT.ordinal()] = 40;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.BLAZE.ordinal()] = 36;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.BOAT.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.CAVE_SPIDER.ordinal()] = 34;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.CHICKEN.ordinal()] = 45;
        }
        catch (NoSuchFieldError noSuchFieldError6) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.COMPLEX_PART.ordinal()] = 61;
        }
        catch (NoSuchFieldError noSuchFieldError7) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.COW.ordinal()] = 44;
        }
        catch (NoSuchFieldError noSuchFieldError8) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.CREEPER.ordinal()] = 25;
        }
        catch (NoSuchFieldError noSuchFieldError9) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.DROPPED_ITEM.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError10) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.EGG.ordinal()] = 56;
        }
        catch (NoSuchFieldError noSuchFieldError11) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDERMAN.ordinal()] = 33;
        }
        catch (NoSuchFieldError noSuchFieldError12) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_CRYSTAL.ordinal()] = 54;
        }
        catch (NoSuchFieldError noSuchFieldError13) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_DRAGON.ordinal()] = 38;
        }
        catch (NoSuchFieldError noSuchFieldError14) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_PEARL.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError15) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_SIGNAL.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError16) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.EXPERIENCE_ORB.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError17) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FALLING_BLOCK.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError18) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FIREBALL.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError19) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FIREWORK.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError20) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FISHING_HOOK.ordinal()] = 57;
        }
        catch (NoSuchFieldError noSuchFieldError21) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.GHAST.ordinal()] = 31;
        }
        catch (NoSuchFieldError noSuchFieldError22) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.GIANT.ordinal()] = 28;
        }
        catch (NoSuchFieldError noSuchFieldError23) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.HORSE.ordinal()] = 52;
        }
        catch (NoSuchFieldError noSuchFieldError24) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.IRON_GOLEM.ordinal()] = 51;
        }
        catch (NoSuchFieldError noSuchFieldError25) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ITEM_FRAME.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError26) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.LEASH_HITCH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError27) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.LIGHTNING.ordinal()] = 58;
        }
        catch (NoSuchFieldError noSuchFieldError28) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MAGMA_CUBE.ordinal()] = 37;
        }
        catch (NoSuchFieldError noSuchFieldError29) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError30) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_CHEST.ordinal()] = 20;
        }
        catch (NoSuchFieldError noSuchFieldError31) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_COMMAND.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError32) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_FURNACE.ordinal()] = 21;
        }
        catch (NoSuchFieldError noSuchFieldError33) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_HOPPER.ordinal()] = 23;
        }
        catch (NoSuchFieldError noSuchFieldError34) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_MOB_SPAWNER.ordinal()] = 24;
        }
        catch (NoSuchFieldError noSuchFieldError35) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_TNT.ordinal()] = 22;
        }
        catch (NoSuchFieldError noSuchFieldError36) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MUSHROOM_COW.ordinal()] = 48;
        }
        catch (NoSuchFieldError noSuchFieldError37) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.OCELOT.ordinal()] = 50;
        }
        catch (NoSuchFieldError noSuchFieldError38) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PAINTING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError39) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PIG.ordinal()] = 42;
        }
        catch (NoSuchFieldError noSuchFieldError40) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PIG_ZOMBIE.ordinal()] = 32;
        }
        catch (NoSuchFieldError noSuchFieldError41) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PLAYER.ordinal()] = 60;
        }
        catch (NoSuchFieldError noSuchFieldError42) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PRIMED_TNT.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError43) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SHEEP.ordinal()] = 43;
        }
        catch (NoSuchFieldError noSuchFieldError44) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SILVERFISH.ordinal()] = 35;
        }
        catch (NoSuchFieldError noSuchFieldError45) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SKELETON.ordinal()] = 26;
        }
        catch (NoSuchFieldError noSuchFieldError46) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SLIME.ordinal()] = 30;
        }
        catch (NoSuchFieldError noSuchFieldError47) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SMALL_FIREBALL.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError48) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SNOWBALL.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError49) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SNOWMAN.ordinal()] = 49;
        }
        catch (NoSuchFieldError noSuchFieldError50) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SPIDER.ordinal()] = 27;
        }
        catch (NoSuchFieldError noSuchFieldError51) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SPLASH_POTION.ordinal()] = 55;
        }
        catch (NoSuchFieldError noSuchFieldError52) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SQUID.ordinal()] = 46;
        }
        catch (NoSuchFieldError noSuchFieldError53) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.THROWN_EXP_BOTTLE.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError54) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.UNKNOWN.ordinal()] = 62;
        }
        catch (NoSuchFieldError noSuchFieldError55) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.VILLAGER.ordinal()] = 53;
        }
        catch (NoSuchFieldError noSuchFieldError56) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WEATHER.ordinal()] = 59;
        }
        catch (NoSuchFieldError noSuchFieldError57) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WITCH.ordinal()] = 41;
        }
        catch (NoSuchFieldError noSuchFieldError58) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WITHER.ordinal()] = 39;
        }
        catch (NoSuchFieldError noSuchFieldError59) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WITHER_SKULL.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError60) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WOLF.ordinal()] = 47;
        }
        catch (NoSuchFieldError noSuchFieldError61) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ZOMBIE.ordinal()] = 29;
        }
        catch (NoSuchFieldError noSuchFieldError62) {}
        return DamageListener.$SWITCH_TABLE$org$bukkit$entity$EntityType = $switch_TABLE$org$bukkit$entity$EntityType2;
    }
}
