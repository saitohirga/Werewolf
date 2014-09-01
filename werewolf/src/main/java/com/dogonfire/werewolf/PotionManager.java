package com.dogonfire.werewolf;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.Random;

public class PotionManager
{
    private Werewolf plugin;
    private Random random;
    
    PotionManager(final Werewolf plugin) {
        super();
        this.random = new Random();
        this.plugin = plugin;
    }
    
    public ItemStack createWerewolfInfectionPotion() {
        final ItemStack potion = new ItemStack(Material.POTION);
        final PotionMeta potionMeta = (PotionMeta)potion.getItemMeta();
        potionMeta.setDisplayName(ChatColor.GOLD + "Werewolf infection potion");
        final List<String> pages = new ArrayList<String>();
        pages.add(ChatColor.GRAY + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InfectionPotionDescription1));
        pages.add(ChatColor.GRAY + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InfectionPotionDescription2));
        potionMeta.setLore((List)pages);
        potionMeta.setMainEffect(PotionEffectType.CONFUSION);
        potion.setItemMeta((ItemMeta)potionMeta);
        return potion;
    }
    
    public ItemStack createWerewolfCurePotion() {
        final ItemStack potion = new ItemStack(Material.POTION);
        final PotionMeta potionMeta = (PotionMeta)potion.getItemMeta();
        potionMeta.setDisplayName(ChatColor.GOLD + "Werewolf cure potion");
        final List<String> pages = new ArrayList<String>();
        pages.add(ChatColor.GRAY + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CurePotionDescription1));
        pages.add(ChatColor.GRAY + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CurePotionDescription2));
        potionMeta.setLore((List)pages);
        potionMeta.setMainEffect(PotionEffectType.CONFUSION);
        potion.setItemMeta((ItemMeta)potionMeta);
        return potion;
    }
}
